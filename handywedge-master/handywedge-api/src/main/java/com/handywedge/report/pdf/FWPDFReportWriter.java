/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.report.pdf;

import java.io.OutputStream;

/**
 * 帳票出力のインターフェースです。 使用方法は以下のとおりです。
 *
 * <pre>
 *
 * {@code    @Inject}
 * {@code    private FWPDFReportWriter writer;
 *     ・・・
 * 
 *     // 帳票情報の設定
 *     FWPDFReport report = new FWPDFReport(templateFile);            //テンプレートファイル名
 *     Map<String, Object> param = new HashMap<>();
 *     param.put("title", "タイトル");
 *     report.setParameters(param);                             //一般項目情報の設定
 *     HogePdf info = new HogePdf();
 *     info.setId("01");
 *     info.setName("Hanako Yamada");
 *     report.addDetail(info);                              //詳細行情報の設定
 * 
 *     // 帳票の設定
 *     writer.addReport(report);
 * 
 *     // 引数のOutputStreamに帳票を出力
 *     writer.print(outputStream);
 *     }
 * </pre>
 */
public interface FWPDFReportWriter {

  /**
   * 帳票データ(FWPDFReportオブジェクト)を追加します。
   *
   * @param report 帳票データ
   * @return 帳票データ総数
   */
  int addReport(FWPDFReport report);

  /**
   * OutputStreamを対象に帳票出力を実行します。
   *
   * @param os 出力対象ストリーム
   */
  void print(OutputStream os);

}
