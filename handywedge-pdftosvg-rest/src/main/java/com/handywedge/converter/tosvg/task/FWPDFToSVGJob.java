package com.handywedge.converter.tosvg.task;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.utils.FWConstantCode;
import com.handywedge.converter.tosvg.utils.FWConverterConst;
import com.handywedge.converter.tosvg.utils.FWConverterUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * PDFファイルをSVGファイルへ変換
 */
public class FWPDFToSVGJob {
  private static final Logger logger = LogManager.getLogger(FWPDFToSVGJob.class);

  private static final String SPACE = " ";
  private static final int TIME_OUT = 10;

  public FWPDFToSVGJob() {}

  private static String[] buildCommand(String inputFile, String outputFile) {
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
        .append("\"").append(outputFile).append("\"").append(SPACE).append("all").append(SPACE);

    commands.add(sb.toString());

    return commands.toArray(new String[0]);
  }

  public List<File> converter(File pdfFile) throws FWConvertProcessException {
    //final String baseName = Objects.requireNonNull(FilenameUtils.getBaseName(pdfFile.getName()));
    final String newBaseName = UUID.randomUUID().toString().replace("-", "");

    File svgTempDir = new File(FWConverterUtils.getWorkDirectory());
    String svgTempDirName = svgTempDir.getAbsolutePath();
    String svgTempFileName = newBaseName + "_%04d." + FWConverterConst.EXTENSION_SVG;

    String[] commands =
        buildCommand(pdfFile.getAbsolutePath(), svgTempDirName + File.separator + svgTempFileName);

    logger.debug("Converter Command: {}", String.join(" ", commands));
    ProcessBuilder pb = new ProcessBuilder(commands);

    Process process = null;
    boolean success = false;
    try {
      process = pb.start();
      success = process.waitFor(TIME_OUT, TimeUnit.SECONDS);
    } catch (IOException | InterruptedException e) {
      deleteSVGFiles(svgTempDir, newBaseName, FWConverterConst.EXTENSION_SVG);
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL, e);
    } finally {
      process.destroy();
    }

    if (!success) {
      deleteSVGFiles(svgTempDir, newBaseName, FWConverterConst.EXTENSION_SVG);
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_TIMEOUT);
    }

    List<File> svgFiles = searchSVGFiles(svgTempDir, newBaseName, FWConverterConst.EXTENSION_SVG);
    if (ObjectUtils.isEmpty(svgFiles)) {
      deleteSVGFiles(svgTempDir, newBaseName, FWConverterConst.EXTENSION_SVG);
      String message =
          String.format("Failure no converted files: {} [{}b]", pdfFile, pdfFile.length());
      logger.debug(message);
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL);
    } else {
      logger.debug("Successful conversion: {} [{}b] to \n{}", pdfFile, pdfFile.length(),
          svgFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.joining("\n")));
    }

    return svgFiles;
  }

  private void deleteSVGFiles(File tempDir, String prefix, String suffix) {
    IOFileFilter fileNameFilter =
      FileFilterUtils.and(
        FileFilterUtils.prefixFileFilter(prefix),
        FileFilterUtils.suffixFileFilter("." + suffix));
    Collection<File> files =
        FileUtils.listFiles(tempDir, fileNameFilter, null);
    files.stream()
      .filter(File::isFile)
      .forEach(
        file -> {
          FileUtils.deleteQuietly(file);
        });
  }

  private List<File> searchSVGFiles(File tempDir, String prefix, String suffix) {
    List<File> svgFiles = new LinkedList<File>();

    IOFileFilter fileNameFilter =
      FileFilterUtils.and(
        FileFilterUtils.prefixFileFilter(prefix),
        FileFilterUtils.suffixFileFilter("." + suffix));
    Collection<File> files =
      FileUtils.listFiles(tempDir, fileNameFilter, null);
    svgFiles = files.stream()
      .filter(File::isFile)
      .sorted(Comparator.comparing(File::getAbsolutePath))
      .collect(Collectors.toList());

    return svgFiles;
  }
}

