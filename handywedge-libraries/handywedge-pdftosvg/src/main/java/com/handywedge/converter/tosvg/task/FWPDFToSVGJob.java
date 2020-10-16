package com.handywedge.converter.tosvg.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.handywedge.converter.tosvg.exceptions.FWConvertTimeoutException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.handywedge.converter.tosvg.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;


/**
 * PDFファイルをSVGファイルへ変換
 */
public class FWPDFToSVGJob {
  private static final FWLogger logger = FWLoggerFactory.getLogger(FWPDFToSVGJob.class);

  private static final String SPACE = " ";
  private static final int TIME_OUT = 10;

  public FWPDFToSVGJob() {}

  public List<File> converter(File pdfFile)
      throws IOException, InterruptedException, FWConvertTimeoutException {
    final long startTime = System.currentTimeMillis();

    final String baseName = Objects.requireNonNull(FilenameUtils.getBaseName(pdfFile.getName()));

    Path svgTempDir = null;
    try {
      svgTempDir = Files.createTempDirectory(null);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("create temporary folder error.");
      return null;
    }
    String svgTempDirName = svgTempDir.toAbsolutePath().toString();
    String svgTempFileName = baseName + "_%05d." + FWConverterConst.EXTENSION_SVG;

    String[] commands =
        buildCommand(pdfFile.getAbsolutePath(), svgTempDirName + File.separator + svgTempFileName);

    logger.info("Converter Command: {}", String.join(" ", commands));
    ProcessBuilder pb = new ProcessBuilder(commands);


      Process process = pb.start();
      boolean success = process.waitFor(TIME_OUT, TimeUnit.SECONDS);
      if (!success) {
        logger.error("converter time out.");
        process.destroy();
        throw new FWConvertTimeoutException();
      }

    List<File> svgFiles = (List<File>) FileUtils.listFiles(svgTempDir.toFile(),
        FileFilterUtils.suffixFileFilter("." + FWConverterConst.EXTENSION_SVG),
        FileFilterUtils.trueFileFilter());

    svgFiles = (List<File>) svgFiles.stream()
        .sorted(Comparator.comparing(File::getAbsolutePath))
        .collect(Collectors.toList());
    if (ObjectUtils.isEmpty(svgFiles)) {
      logger.error("Failure conversion: {} [{}b]", pdfFile, pdfFile.length());
    }else{
      logger.info("Successful conversion: {} [{}b] to \n{}", pdfFile, pdfFile.length(),
          svgFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.joining("\n")));
    }

    final long endTime = System.currentTimeMillis();
    logger.info(" [PDF => SVG] Converter ExecutionTime: {}ms", (endTime - startTime));

    return svgFiles;
  }

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
    sb.append("pdf2svg").append(SPACE)
        .append("\"").append(inputFile).append("\"").append(SPACE)
        .append("\"").append(outputFile).append("\"").append(SPACE)
        .append("all").append(SPACE);

    commands.add(sb.toString());

    return commands.toArray(new String[0]);
  }

  public void deleteFiles(List<File> svgFiles) {
    if (ObjectUtils.isEmpty(svgFiles)) {
      return;
    }

    File path = svgFiles.stream().findFirst().get().getParentFile();

    svgFiles.stream().forEach(svg -> {
      if (svg != null && svg.exists()) {
        svg.delete();
      }
    });

    if (path.exists() && path.isDirectory()) {
      path.delete();
    }

    return;
  }
}
