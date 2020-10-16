package com.handywedge.converter;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.handywedge.converter.task.FWPDFToSVGJob;
import com.handywedge.converter.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;

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
  public List<File> pdfToSvg(File sourceFile) {
    List<File> targetFiles = null;

    logger.info("=== Converter Start. ===");
    final long startTime = System.currentTimeMillis();

    if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
      logger.warn("can not read source file: {}", sourceFile);
      return targetFiles;
    }

    // logger.info("Converter Source File: {}", sourceFile.getAbsolutePath());

    // 拡張子判別
    final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

    if (!isPDF(inputExtension)) {
      logger.warn("not supported extension: {}", inputExtension);
      return targetFiles;
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
