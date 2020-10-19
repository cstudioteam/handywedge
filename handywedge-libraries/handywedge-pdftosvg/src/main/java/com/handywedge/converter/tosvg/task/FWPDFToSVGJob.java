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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.handywedge.common.FWConstantCode;
import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
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
    final String baseName = Objects.requireNonNull(FilenameUtils.getBaseName(pdfFile.getName()));

    // FIXME 一次ディレクトリの削除漏れ？
    Path svgTempDir = null;
    try {
      svgTempDir = Files.createTempDirectory(null);
    } catch (IOException e) {
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL, e);
    }
    String svgTempDirName = svgTempDir.toAbsolutePath().toString();
    String svgTempFileName = baseName + "_%05d." + FWConverterConst.EXTENSION_SVG;

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
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL, e);
    } finally {
      process.destroy();
    }

    if (!success) {
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_TIMEOUT);
    }

    List<File> svgFiles = (List<File>) FileUtils.listFiles(svgTempDir.toFile(),
        FileFilterUtils.suffixFileFilter("." + FWConverterConst.EXTENSION_SVG),
        FileFilterUtils.trueFileFilter());

    svgFiles = svgFiles.stream().sorted(Comparator.comparing(File::getAbsolutePath))
        .collect(Collectors.toList());
    if (ObjectUtils.isEmpty(svgFiles)) {
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

}
