package com.handywedge.converter.tosvg;

import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.exceptions.FWConvertTimeoutException;
import com.handywedge.converter.tosvg.exceptions.FWUnsupportedFormatException;
import com.handywedge.converter.tosvg.task.FWPDFToSVGJob;
import com.handywedge.converter.tosvg.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * ドキュメント変換クラス
 * 変換対象ファイル： PDF,MS Office(ワード、エクセル、パワポイント)
 * 変換ファイル： SVG
 */
public class FWDocumentConverter {

  private static final FWLogger logger = FWLoggerFactory.getLogger(FWDocumentConverter.class);

  /**
   * PDFファイルをSVGファイルへ変換
   *
   * @param sourceFile 変換元PDFファイル
   * @return 変換したsvgファイル
   */
  public List<File> pdfToSvg(File sourceFile)
      throws FWUnsupportedFormatException, FWConvertTimeoutException, FWConvertProcessException {
    List<File> targetFiles = null;

    logger.info("=== Converter Start. ===");
    final long startTime = System.currentTimeMillis();

    if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
      String message = String.format("can not read source file: %s", sourceFile);
      logger.error(message);
      throw new FWConvertProcessException(message);
    }

    // 拡張子判別
    final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

    if (!isPDF(inputExtension)) {
      String message = String.format("Unsupported extension: [%s]", inputExtension);
      logger.error(message);
      throw new FWUnsupportedFormatException(message);
    }

    // PDFのSVG変換
    FWPDFToSVGJob toSVGJob = new FWPDFToSVGJob();
    targetFiles = toSVGJob.converter(sourceFile);

    final long endTime = System.currentTimeMillis();
    logger.info(" DocumentConverter ExecutionTime: {}ms", endTime - startTime);

    logger.info("=== Converter end. ===");
    return targetFiles;
  }

  private boolean isPDF(String extension) {
    if (StringUtils.isEmpty(extension)) {
      return false;
    }

    return FWConverterConst.PDF_DOCUMENT_EXTENSIONS.contains(StringUtils.lowerCase(extension));
  }
}
