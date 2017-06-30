/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.activation.DataSource;

/**
 * フレームワーク内部で使用するクラスです。<br>
 * アプリケーションでは使用しないで下さい。
 */
public class FWAttachmentDataSource implements DataSource, Serializable {

  private static final long serialVersionUID = 1L;

  /** ファイル本体 */
  private byte[] buffer;
  /** ファイル名 */
  private String name;
  /** ファイルサイズ */
  private int size;
  /** コンテントタイプ */
  private String contentType;
  /** java.io.InputStream型 */
  private transient InputStream is;
  /** java.io.OutputStream型 */
  private transient OutputStream os;

  /**
   * コンストラクタ
   *
   * @param buffer バイト配列
   * @param name ファイル名
   */
  public FWAttachmentDataSource(byte[] buffer, String name) {

    this.buffer = buffer;
    this.name = name;
    this.size = buffer.length;
    this.contentType = "application/octet-stream";
  }

  /**
   * バイナリデータを入力ストリームで取得する。
   *
   * @return java.io.InputStream型
   * @throws IOException 入出力エラー
   */
  @Override
  public InputStream getInputStream() throws IOException {

    is = new ByteArrayInputStream(buffer);
    return is;
  }

  /**
   * バイナリデータを出力ストリームで取得する。
   *
   * @return java.io.OutputStream型
   * @throws IOException 入出力エラー
   */
  @Override
  public OutputStream getOutputStream() throws IOException {

    os = new ByteArrayOutputStream();
    os.write(buffer);
    os.flush();
    return os;
  }

  /**
   * ファイル名を取得する。
   *
   * @return ファイル名
   */
  @Override
  public String getName() {

    return this.name;
  }

  /**
   * コンテントタイプを取得する。
   *
   * @return コンテントタイプ
   */
  @Override
  public String getContentType() {

    return this.contentType;
  }

  /**
   * ファイルサイズを取得する。
   *
   * @return ファイルサイズ
   */
  public int getSize() {

    return this.size;
  }

  /**
   * InputStream、OutputStreamを閉じる。
   */
  public void close() {

    if (is != null) {
      try {
        is.close();
      } catch (Exception e) {
      }
    }
    if (os != null) {
      try {
        os.close();
      } catch (Exception e) {
      }
    }
  }
}
