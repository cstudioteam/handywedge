package com.csframe.common;

public interface FWConstantCode {

  /**
   * 補足できない想定外のエラーが発生。
   */
  String FATAL = "00001F";

  /**
   * web.xmlにcsf.app_idの設定がない。
   */
  String NO_APP_ID = "00002F";

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

  /* REST機能のFWコード */

  /**
   * 予期しないエラー。
   */
  int FW_REST_ERROR = -9000;

  /**
   * サポートされていないHTTPメソッド。
   */
  int FW_REST_UNSUPPORTED = -9001;

}
