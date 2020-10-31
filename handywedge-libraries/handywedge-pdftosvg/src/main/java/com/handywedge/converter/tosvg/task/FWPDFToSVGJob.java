package com.handywedge.converter.tosvg.task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.ObjectUtils;

import com.handywedge.common.FWConstantCode;
import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;
import org.apache.pdfbox.pdmodel.PDDocument;


/**
 * PDFファイルをSVGファイルへ変換
 */
public class FWPDFToSVGJob {
  private static final FWLogger logger = FWLoggerFactory.getLogger(FWPDFToSVGJob.class);

  private static final String SPACE = " ";
  private static final int PAGE_CONVERTER_TIME_OUT = 3;

  private static final int DELETE_DILE_BEFORE_DAYS = 7;

  private final int pageThreshold;
  private final int threadCount;

  /**
   * コンストラクター
   * @param pageThreshold ページ数しきい値
   * @param threadCount 並行スレッド数
   */
  public FWPDFToSVGJob(int pageThreshold, int threadCount){
    this.pageThreshold = pageThreshold;
    this.threadCount = threadCount;
  }

  private static String[] buildCommand(String inputFile, String outputFile, int page) {
    List<String> commands = new ArrayList<String>();

    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    if (isWindows) {
      commands.add("cmd.exe");
      commands.add("/c");
    } else {
      commands.add("sh");
      commands.add("-c");
    }

    StringBuilder sb = new StringBuilder();
    sb.append("pdf2svg").append(SPACE).append("\"").append(inputFile).append("\"").append(SPACE)
        .append("\"").append(outputFile).append("\"").append(SPACE).append(page).append(SPACE);

    commands.add(sb.toString());

    return commands.toArray(new String[0]);
  }

  private int getPdfFilePageCount(File pdfFile){
    int pages = 0;

    long start = System.currentTimeMillis();
    PDDocument document= null;
    try {
      document = PDDocument.load(pdfFile);
      pages = document.getNumberOfPages();
    } catch (IOException e) {
      logger.warn("Load PDFFile Pages error.");
    }finally{
      if(ObjectUtils.isNotEmpty(document)){
        try {
          document.close();
        } catch (IOException e) {
          logger.warn("Close loaded PDFFile Pages error.");
        }
      }
    }

    long end = System.currentTimeMillis();
    logger.info("Load PDFFile Pages: {}, time: {} ms", pages, end - start);
    return pages;
  }

  public List<File> converter(File pdfFile, Integer timeout) throws FWConvertProcessException {
    final String newBaseName = UUID.randomUUID().toString().replace("-", "");

    int pageCount = getPdfFilePageCount(pdfFile);
    File svgTempDir = FileUtils.getTempDirectory();
    String svgTempDirName = svgTempDir.getAbsolutePath();

    clearOldConverterFiles(svgTempDir, FWConverterConst.EXTENSION_SVG, DELETE_DILE_BEFORE_DAYS);
    clearOldConverterFiles(svgTempDir, FWConverterConst.EXTENSION_PDF, DELETE_DILE_BEFORE_DAYS);

    int threads = 1;
    if(pageCount >= this.pageThreshold){
      threads = this.threadCount;
      logger.info("Over {} pages, converter use {} threads.", this.pageThreshold, threads);
    }else{
      logger.info("Less than {} pages, converter use {} threads.", this.pageThreshold, threads);
      threads = ForkJoinPool.getCommonPoolParallelism();
    }

    ForkJoinPool forkJoinPool = null;
    try {
      forkJoinPool = new ForkJoinPool(threads);
      forkJoinPool.submit(() -> {
        IntStream.range(1, pageCount+1)
            .parallel()
            .forEach(page -> {
              long start = System.currentTimeMillis();

              String svgTempFileName = newBaseName + String.format("_%04d.", page) + FWConverterConst.EXTENSION_SVG;
              String[] commands = buildCommand(pdfFile.getAbsolutePath(), svgTempDirName + File.separator + svgTempFileName, page);

              ProcessBuilder pb = new ProcessBuilder(commands);
              Process process = null;
              try {
                process = pb.start();
                process.waitFor(timeout != null ? timeout : PAGE_CONVERTER_TIME_OUT, TimeUnit.SECONDS);
              } catch (IOException | InterruptedException e) {
                logger.warn("Converted file error. page: {}, details: {}", page, e.getMessage());
              } finally {
                process.destroy();
              }

              long end = System.currentTimeMillis();
              logger.debug("Converted file. page: {}, time: {} ms", page, end - start);
            });
      }).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL, e);
    }finally {
      forkJoinPool.shutdown();
      try {
        forkJoinPool.awaitTermination(Long.valueOf(timeout != null ? timeout : PAGE_CONVERTER_TIME_OUT * pageCount), TimeUnit.SECONDS);
      } catch (InterruptedException e) {
      }
    }

    List<File> svgFiles = searchSVGFiles(svgTempDir, newBaseName, FWConverterConst.EXTENSION_SVG);
    if (ObjectUtils.isEmpty(svgFiles)) {
      deleteSVGFiles(svgTempDir, newBaseName, FWConverterConst.EXTENSION_SVG);
      String message =
          String.format("Failure no converted files: %s [%d byte]", pdfFile, pdfFile.length());
      logger.debug(message);
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL);
    } else {
      logger.debug("Successful conversion: {} [{}b] to \n{}", pdfFile, pdfFile.length(),
          svgFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.joining("\n")));
    }

    return svgFiles;
  }

  public List<File> converter(File pdfFile, String endpoint, Integer timeout) throws FWConvertProcessException {
    // pdf file
    final String baseName = Objects.requireNonNull(FilenameUtils.getBaseName(pdfFile.getName()));
    final String baseExtension = FilenameUtils.getExtension(pdfFile.getName());

    File svgTempDir = FileUtils.getTempDirectory();

    // pdf file rename
    final String newBaseName = UUID.randomUUID().toString().replace("-", "");
    File newPDFFile =
        new File(svgTempDir.toString() + File.separator + newBaseName + "." + baseExtension);
    try {
      FileUtils.copyFile(pdfFile, newPDFFile);
    } catch (IOException e) {
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL, e);
    }

    // svg file
    final String svgExtension = FWConverterConst.EXTENSION_SVG;
    List<File> svgFiles = null;

    FWRemoteJobManager remoteManager = new FWRemoteJobManager(endpoint, timeout);
    try {
      List<String> downloadUrls = remoteManager.convert(pdfFile);
      svgFiles = remoteManager.download(downloadUrls, svgTempDir, newBaseName, svgExtension);
    } catch (IOException e) {
      String message = String.format("Failed conversion: %s [%d byte]. details: %s",
          pdfFile, pdfFile.length(), e);
      deleteSVGFiles(svgTempDir, newBaseName, FWConverterConst.EXTENSION_SVG);
      logger.error(message);
      throw new FWConvertProcessException(FWConstantCode.OFFICE_TO_PDF_FAIL, e);
    } finally {
      FileUtils.deleteQuietly(newPDFFile);
    }

    if (ObjectUtils.isEmpty(svgFiles)) {
      deleteSVGFiles(svgTempDir, newBaseName, FWConverterConst.EXTENSION_SVG);
      String message =
          String.format("Failure no converted files: %s [%d byte]", pdfFile, pdfFile.length());
      logger.debug(message);
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL);
    } else {
      logger.debug("Successful conversion: {} [{}b] to \n{}", pdfFile, pdfFile.length(),
          svgFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.joining("\n")));
    }
    return svgFiles;
  }

  private void clearOldConverterFiles(File tempDir, String suffix, int beforeDay) {
    LocalDateTime now =  LocalDateTime.now();
    Date specifyDay = Date.from(now.minusDays(beforeDay).atZone(ZoneId.systemDefault()).toInstant());

    IOFileFilter fileNameFilter =
        FileFilterUtils.and(
            FileFilterUtils.ageFileFilter(specifyDay),
            FileFilterUtils.suffixFileFilter("." + suffix));
    Collection<File> files =
        FileUtils.listFiles(tempDir, fileNameFilter, null);
    if(ObjectUtils.isNotEmpty(files)) {
      logger.info("Delete {} file {} days ago. number: {}", suffix, beforeDay, files.size());
      logger.debug("Deleted files. count: {}, details: {}", files.size(), files);
      files.stream().filter(File::isFile).forEach(file -> {
        FileUtils.deleteQuietly(file);
      });
    }
  }


  private void deleteSVGFiles(File tempDir, String prefix, String suffix) {
    IOFileFilter fileNameFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(prefix),
        FileFilterUtils.suffixFileFilter("." + suffix));
    Collection<File> files = FileUtils.listFiles(tempDir, fileNameFilter, null);
    files.stream().filter(File::isFile).forEach(file -> {
      FileUtils.deleteQuietly(file);
    });
  }

  private List<File> searchSVGFiles(File tempDir, String prefix, String suffix) {
    List<File> svgFiles = new LinkedList<File>();

    IOFileFilter fileNameFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(prefix),
        FileFilterUtils.suffixFileFilter("." + suffix));
    Collection<File> files = FileUtils.listFiles(tempDir, fileNameFilter, null);
    svgFiles = files.stream().filter(File::isFile)
        .sorted(Comparator.comparing(File::getAbsolutePath)).collect(Collectors.toList());

    return svgFiles;
  }
}

