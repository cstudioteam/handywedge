package com.handywedge.converter.tosvg;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.handywedge.common.FWConstantCode;
import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.exceptions.FWUnsupportedFormatException;
import com.handywedge.converter.tosvg.task.FWPDFToSVGJob;
import com.handywedge.converter.tosvg.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;

/**
 * ドキュメント変換クラス
 * 変換対象ファイル： PDF,MS Office(ワード、エクセル、パワポイント)
 * 変換ファイル： SVG
 */
public class FWPdfConverter {

  private static final FWLogger logger = FWLoggerFactory.getLogger(FWPdfConverter.class);

  private int pageThreshold;
  private int threadCount;

  /**
   *　コンストラクター
   */
  public FWPdfConverter(){
    this.pageThreshold = 100;
    this.threadCount = Runtime.getRuntime().availableProcessors() -1;
  }

  /**
   *　コンストラクター
   * @param pageThreshold ページ数しきい値
   * @param threadCount 並行スレッド数
   */
  public FWPdfConverter(int pageThreshold, int threadCount){
    this.pageThreshold = pageThreshold;
    this.threadCount = threadCount;
  }

  /**
   * PDFファイルをSVGファイルへ変換
   *
   * @param sourceFile 変換元PDFファイル
   * @return 変換したsvgファイル
   */
  public List<File> pdfToSvg(File sourceFile)
      throws FWUnsupportedFormatException, FWConvertProcessException {
    return pdfToSvg(sourceFile, null);
  }

  /**
   * PDFファイルをSVGファイルへ変換
   *
   * @param sourceFile 変換元PDFファイル
   * @param timeout 変換処理のタイムアウト値（秒）
   * @return 変換したsvgファイル
   */
  public List<File> pdfToSvg(File sourceFile, Integer timeout)
      throws FWUnsupportedFormatException, FWConvertProcessException {
    final long startTime = logger.perfStart("pdfToSvg");

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
    FWPDFToSVGJob toSVGJob = new FWPDFToSVGJob(this.pageThreshold, this.threadCount);
    List<File> targetFiles = toSVGJob.converter(sourceFile, timeout);

    logger.perfEnd("pdfToSvg", startTime);
    return targetFiles;
  }

  /**
   * PDFファイルをSVGファイルへ変換
   *
   * @param sourceFile 変換元PDFファイル
   * @param endpoint PDF変換RESTのIPアドレス
   * @param timeout 変換処理のタイムアウト値（秒）
   * @return 変換したsvgファイル
   */
  public List<File> pdfToSvg(File sourceFile, String endpoint, Integer timeout)
      throws FWUnsupportedFormatException, FWConvertProcessException {
    final long startTime = logger.perfStart("pdfToSvg");

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
    FWPDFToSVGJob toSVGJob = new FWPDFToSVGJob(this.pageThreshold, this.threadCount);
    List<File> targetFiles = toSVGJob.converter(sourceFile, endpoint, timeout);

    logger.perfEnd("pdfToSvg", startTime);
    return targetFiles;
  }

  private boolean isPDF(String extension) {
    if (StringUtils.isEmpty(extension)) {
      return false;
    }

    return FWConverterConst.PDF_DOCUMENT_EXTENSIONS.contains(StringUtils.lowerCase(extension));
  }
}
