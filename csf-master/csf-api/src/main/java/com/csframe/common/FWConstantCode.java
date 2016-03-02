package com.csframe.common;

public interface FWConstantCode {

  /**
   * 補足できない想定外のエラーが発生した。
   */
  String FATAL = "00001F";

  /**
   * web.xmlにcsf.app_idの設定がない。
   */
  String NO_APP_ID = "00002F";

  /**
   * プロパティファイルに指定されたkeyが存在しない。
   */
  String NO_KEY = "00003E";

  /**
   * 予期しないDB関連のエラー。
   */
  String DB_FATAL = "00101F";

  /**
   * データソースのlookupに失敗。
   */
  String DS_LOOKUP_FAIL = "00102E";


  /* REST機能のFWコード */

  /**
   * 予期しないエラー
   */
  int FW_REST_ERROR = -9000;

  /**
   * サポートされていないHTTPメソッド
   */
  int FW_REST_UNSUPPORTED = -9001;

}
