package com.csframe.report.pdf;

import java.io.OutputStream;

/**
 * 帳票出力のインターフェースです。 使用方法は以下のとおりです。
 *
 * <PRE>
 *
 *   {@code  @Inject}
 * {@code    private FWReportWriter writer;
 *     ・・・
 * 
 *     // 帳票情報の設定
 *     FWReport report = new FWReport(templateFile);            //テンプレートファイル名
 *     Map param = new HashMap();
 *     param.put("title", "タイトル");
 *     report.setParameters(param);                             //一般項目情報の設定
 *     Map details = new HashMap();
 *     details.put("id", "01");
 *     details.put("name", "Hanako Yamada");
 *     report.setDetails(details);                              //詳細行情報の設定
 * 
 *     // 帳票の設定
 *     writer.addReport(report);
 * 
 *     // 引数のOutputStreamに帳票を出力
 *     writer.print(outputStream);
 *     }
 * </PRE>
 */
public interface FWPDFReportWriter {

  /**
   * 帳票データ(FWReportオブジェクト)を追加します。
   *
   * @param report 帳票データ
   * @return 帳票データ総数
   */
  int addReport(FWPDFReport report);

  /**
   * OutputStreamを対象に帳票出力を実行します。
   *
   * @param os 出力対象ストリーム
   * @throws FWReportIllegalStateException 帳票出力の事前準備が不十分な場合
   * @throws FWReportException 帳票出力に失敗した場合
   */
  void print(OutputStream os);

}
