/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.mail;

/**
 * メールエンコーディングの列挙型です。
 */
public enum FWMailCharacterEncoding {

  UTF_8("UTF-8"), ISO_2022_JP("ISO-2022-JP");
  /** 文字コード */
  private String charCd;

  /**
   * コンストラクタ
   *
   * @param charCd 文字コード
   */
  private FWMailCharacterEncoding(String charCd) {

    this.charCd = charCd;
  }

  /**
   * 文字コードを取得する。
   *
   * @return 文字コード
   */
  public String getCharacterEncoding() {

    return charCd;
  }
}
