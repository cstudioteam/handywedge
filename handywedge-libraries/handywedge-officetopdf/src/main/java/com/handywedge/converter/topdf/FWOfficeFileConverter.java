package com.handywedge.converter.topdf;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.handywedge.common.FWConstantCode;
import com.handywedge.converter.topdf.exceptions.FWConvertProcessException;
import com.handywedge.converter.topdf.exceptions.FWUnsupportedFormatException;
import com.handywedge.converter.topdf.task.FWOfficeToPDFJob;
import com.handywedge.converter.topdf.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;

/**
 * ドキュメント変換クラス
 * 変換対象ファイル： MS Office(ワード、エクセル、パワポイント)
 * 変換ファイル： PDF
 */
public class FWOfficeFileConverter {

  private static final FWLogger logger = FWLoggerFactory.getLogger(FWOfficeFileConverter.class);

  /**
   * [ローカル]SVGファイルへ変換
   *
   * @param sourceFile 変換元MS Officeドキュメントファイル
   * @return 変換したPDFファイル
   */
  public File fileToPdf(File sourceFile)
      throws FWUnsupportedFormatException, FWConvertProcessException {
    final long startTime = logger.perfStart("fileToPdf");

    if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
      throw new FWConvertProcessException(FWConstantCode.OFFICE_TO_PDF_UNREAD,
          sourceFile.getAbsolutePath());
    }

    // 拡張子判別
    final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

    if (!isOfficeDocument(inputExtension)) {
      throw new FWUnsupportedFormatException(FWConstantCode.OFFICE_TO_PDF_UNSUPPORTED);
    }

    // MS OfficeドキュメントのPDF変換
    FWOfficeToPDFJob toPDFJob = new FWOfficeToPDFJob();
    File pdfFile = toPDFJob.converter(sourceFile);

    logger.perfEnd("fileToPdf", startTime);
    return pdfFile;
  }

  /**
   * [リモート]SVGファイルへ変換
   *
   * @param sourceFile 変換元ファイル
   * @param endpoint LibreOfficeサーバーIPアドレス
   * @return 変換したsvgファイル
   */
  public File fileToPdf(File sourceFile, String endpoint)
      throws FWUnsupportedFormatException, FWConvertProcessException {
    return fileToPdf(sourceFile, endpoint, null);
  }

  /**
   * [リモート]SVGファイルへ変換
   *
   * @param sourceFile 変換元ファイル
   * @param endpoint LibreOfficeサーバーIPアドレス
   * @param リモート呼び出し時のタイムアウト値（秒）
   * @return 変換したsvgファイル
   */
  public File fileToPdf(File sourceFile, String endpoint, Integer timeout)
      throws FWUnsupportedFormatException, FWConvertProcessException {
    final long startTime = logger.perfStart("fileToPdf");

    if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
      throw new FWConvertProcessException(FWConstantCode.OFFICE_TO_PDF_UNREAD,
          sourceFile.getAbsolutePath());
    }

    // 拡張子判別
    final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

    if (!isOfficeDocument(inputExtension)) {
      throw new FWUnsupportedFormatException(FWConstantCode.OFFICE_TO_PDF_UNSUPPORTED);
    }

    // MS OfficeドキュメントのPDF変換
    FWOfficeToPDFJob toPDFJob = new FWOfficeToPDFJob();
    File pdfFile = toPDFJob.converter(sourceFile, endpoint, timeout);

    logger.perfEnd("fileToPdf", startTime);
    return pdfFile;
  }

  private boolean isOfficeDocument(String extension) {
    if (StringUtils.isEmpty(extension)) {
      return false;
    }

    return FWConverterConst.OFFICE_DOCUMENT_EXTENSIONS.contains(StringUtils.lowerCase(extension));
  }

}
