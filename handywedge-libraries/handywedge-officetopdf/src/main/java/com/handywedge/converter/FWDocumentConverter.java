package com.handywedge.converter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jodconverter.core.office.OfficeException;

import com.handywedge.converter.task.FWOfficeToPDFJob;
import com.handywedge.converter.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;

/**
 * ドキュメント変換クラス
 * 変換対象ファイル： MS Office(ワード、エクセル、パワポイント)
 * 変換ファイル： PDF
 */
public class FWDocumentConverter {

  private static final FWLogger logger = FWLoggerFactory.getLogger(FWDocumentConverter.class);

  /**
   * [ローカル]SVGファイルへ変換
   *
   * @param sourceFile 変換元MS Officeドキュメントファイル
   * @return 変換したPDFファイル
   */
  public File fileToPdf(File sourceFile) throws IOException, OfficeException {
    File pdfFile = null;

    logger.info("=== Converter Start. ===");
    final long startTime = System.currentTimeMillis();

    if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
      logger.warn("can not read source file: {}", sourceFile);
      return pdfFile;
    }

    // 拡張子判別
    final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

    if (!isOfficeDocument(inputExtension)) {
      logger.warn("not supported extension: {}", inputExtension);
      return pdfFile;
    }

    // MS OfficeドキュメントのPDF変換
    FWOfficeToPDFJob toPDFJob = new FWOfficeToPDFJob();
    pdfFile = toPDFJob.converter(sourceFile);

    final long endTime = System.currentTimeMillis();
    logger.info(" DocumentConverter ExecutionTime: {}ms", endTime - startTime);

    logger.info("=== Converter end. ===");
    return pdfFile;
  }

  /**
   * [リモート]SVGファイルへ変換
   *
   * @param sourceFile 変換元ファイル
   * @param endpoint LibreOfficeサーバーIPアドレス
   * @return 変換したsvgファイル
   */
  public File fileToPdf(File sourceFile, String endpoint) throws IOException, OfficeException {
    File pdfFile = null;

    logger.info("=== Converter Start. ===");
    final long startTime = System.currentTimeMillis();

    if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
      logger.warn("can not read source file: {}", sourceFile);
      return pdfFile;
    }

    // 拡張子判別
    final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

    if (!isOfficeDocument(inputExtension)) {
      logger.warn("not supported extension: {}", inputExtension);
      return pdfFile;
    }

    // MS OfficeドキュメントのPDF変換
    FWOfficeToPDFJob toPDFJob = new FWOfficeToPDFJob();
    pdfFile = toPDFJob.converter(sourceFile, endpoint);

    final long endTime = System.currentTimeMillis();
    logger.info(" DocumentConverter ExecutionTime: {}ms", endTime - startTime);

    logger.info("=== Converter end. ===");
    return pdfFile;
  }

  private boolean isOfficeDocument(String extension) {
    if (StringUtils.isEmpty(extension)) {
      return false;
    }

    return FWConverterConst.OFFICE_DOCUMENT_EXTENSIONS.contains(StringUtils.lowerCase(extension));
  }

}
