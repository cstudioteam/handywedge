/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.common;

/**
 * csFrameのエラーコードとREST機能のリターンコードを定義するインターフェースです。<br>
 * エラーコードの末尾はF=Fatal、E=Exceptionを示し、Fatalはシステムで予期しないエラーとなります。<br>
 */
public interface FWConstantCode {

  /**
   * 補足できない想定外のエラーが発生。
   */
  String FATAL = "00001F";

  /**
   * web.xmlにcsf.app_idの設定がない。
   */
  String NO_APP_ID = "00002E";

  /**
   * 予期しないDB関連のエラー。
   */
  String DB_FATAL = "00101F";

  /**
   * データソースのlookupに失敗。
   */
  String DS_LOOKUP_FAIL = "00102E";

  /**
   * プロパティファイルに指定されたkeyが存在しない。
   */
  @Deprecated
  String PROPERTIES_KEY_MISSING = "00201E";

  /**
   * 帳票テンプレートファイルが見つからない。
   */
  String PDF_TEMPLATE_FILE_MISSING = "00301E";

  /**
   * 帳票テンプレートファイルの読み込みエラー。
   */
  String PDF_TEMPLATE_LOAD_FAIL = "00302E";

  /**
   * PDF生成時にエラー。
   */
  String PDF_OUTPUT_FAIL = "00303E";

  /**
   * 帳票データがセットされていない。
   */
  String PDF_DATA_MISSING = "00304E";

  /**
   * 暗号処理でエラー。
   */
  String CIPHER_ENCRYPT_FAIL = "00401E";

  /**
   * 復号処理でエラー。
   */
  String CIPHER_DECRYPT_FAIL = "00402E";

  /**
   * SMTPサーバーのホスト名設定がない。
   */
  String MAIL_HOST_MISSING = "00501E";

  /**
   * メール宛先アドレスのパラメータがない。
   */
  String MAIL_ADDR_MISSING = "00502E";

  /**
   * メール添付ファイルがサイズオーバー。
   */
  String MAIL_FILE_SIZE_OVER = "00503E";

  /**
   * メール送信処理でエラー。
   */
  String MAIL_SEND_FAIL = "00504E";

  /* REST機能のFWコード */

  /**
   * 予期しないエラー。
   */
  int FW_REST_ERROR = -9000;

  /**
   * サポートされていないHTTPメソッド。
   */
  int FW_REST_UNSUPPORTED = -9001;

  /**
   * リクエストされたパス・クラス名が存在しない。
   */
  int FW_REST_ROOTING_ERROR = -9002;

  /**
   * ユーザーID・パスワード認証に失敗しトークン発行ができない。
   */
  int FW_REST_TOKENPUB_UNAUTHORIZED = -9003;

  /**
   * パラメータが不足している。
   */
  int FW_REST_TOKENPUB_INVALID = -9004;

  /**
   * ユーザーIDの文字数が長すぎる。
   */
  int FW_REST_USER_REG_ID_INVALID = -9005;

  /**
   * ユーザーIDがすでに存在している。（重複）
   */
  int FW_REST_USER_REG_ID_EXISTS = -9006;

  /**
   * パラメータが不足している。
   */
  int FW_REST_USER_CHANGE_PASSWD_INVALID = -9007;

  /**
   * パスワード認証に失敗。
   */
  int FW_REST_USER_CHANGE_PASSWD_UNAUTHORIZED = -9008;

}
