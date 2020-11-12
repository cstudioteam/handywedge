package com.handywedge.converter.tosvg.library;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.handywedge.converter.tosvg.library.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.library.exceptions.FWUnsupportedFormatException;
import com.handywedge.converter.tosvg.library.task.FWPDFToSVGJob;
import com.handywedge.converter.tosvg.library.utils.FWConstantCode;
import com.handywedge.converter.tosvg.library.utils.FWConverterConst;

/**
 * ドキュメント変換クラス
 * 変換対象ファイル： PDF,MS Office(ワード、エクセル、パワポイント)
 * 変換ファイル： SVG
 */
public class FWPdfConverter {

  private static final Logger logger = LogManager.getLogger(FWPdfConverter.class);

  /**
   * PDFファイルをSVGファイルへ変換
   *
   * @param sourceFile 変換元PDFファイル
   * @return 変換したsvgファイル
   */
  public List<File> pdfToSvg(File sourceFile)
      throws FWUnsupportedFormatException, FWConvertProcessException {
    final long startTime = System.currentTimeMillis();
    logger.info("{}() start.", "pdfToSvg");

    if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
      throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_UNREAD,
          sourceFile.getAbsolutePath());
    }

    // 拡張子判別
    final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

    if (!isPDF(inputExtension)) {
      throw new FWUnsupportedFormatException(FWConstantCode.PDF_TO_SVG_UNSUPPORTED);
    }

    // PDFのSVG変換
    FWPDFToSVGJob toSVGJob = new FWPDFToSVGJob();
    List<File> targetFiles = toSVGJob.converter(sourceFile);

    logger.info("{}() end.\tElapsedTime[{}]ms", "pdfToSvg", System.currentTimeMillis() - startTime);
    return targetFiles;
  }

  private boolean isPDF(String extension) {
    if (StringUtils.isEmpty(extension)) {
      return false;
    }

    return FWConverterConst.PDF_DOCUMENT_EXTENSIONS.contains(StringUtils.lowerCase(extension));
  }
}
