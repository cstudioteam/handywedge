/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.report.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
/**
 * 帳票の情報を保持するクラスです。
 */
public class FWPDFReport {

  /**
   * クラスパスから取得できるテンプレートファイルのパス。
   * 
   * @return テンプレートファイルパス
   */
  private String templateFile;
  /**
   * 一般項目情報。
   * 
   * @param parameter 一般項目に出力する情報
   * @return 一般項目に出力する情報
   */
  @Setter
  private Map<String, Object> parameters;
  /**
   * 明細行情報。
   * 
   * @return 明細行として出力する情報のリスト
   */
  private List details = new ArrayList();

  /**
   * 帳票テンプレートファイル名を引数に取るコンストラクタです。
   *
   * @param templateFile 帳票テンプレートファイル名
   */
  public FWPDFReport(String templateFile) {

    this.templateFile = templateFile;
  }

  /**
   * 明細行の情報を追加します(フィールド名と紐付くアクセサを持ったDTO)。<br>
   *
   * @param detail 明細行として出力する情報
   */
  public void addDetail(Object detail) {

    details.add(detail);
  }

  /**
   * 複数の明細行の情報を追加します(フィールド名と紐付くアクセサを持ったDTOのリスト)。<br>
   *
   * @param details 明細行として出力する情報のリスト
   */
  public void addAllDetails(List details) {

    this.details.addAll(details);
  }
}
