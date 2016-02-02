package jp.cstudio.csfw.context;

import java.util.Date;

import jp.cstudio.csfw.user.FWUser;

/**
 * フレームワーク持ち回り情報の業務アクセス用インターフェースです。<br>
 * 下記の情報を取得出来ます。<br>
 * <ul>
 * <li>ログイン中のユーザ情報</li>
 * <li>リクエストに関する情報</li>
 * <li>セッションに関する情報</li>
 * <li>アプリケーションに関する情報</li>
 * </ul>
 * また、同一セッション内での持ち回り情報を setAttribute、getAttributeメソッドで設定／取得出来ます。
 */
public interface FWContext {

  /**
   * リクエストIDを取得する。
   *
   * @return リクエストID
   */
  String getRequestId();

  /**
   * リクエスト開始時刻を取得する。
   *
   * @return リクエスト開始時刻
   */
  Date getRequestStartTime();

  /**
   * 実行中のサーバーの名称を取得する。
   *
   * @return 実行中のサーバーの名称
   */
  String getHostName();

  /**
   * アプリケーションIDを取得する。
   *
   * @return アプリケーションID
   */
  String getApplicationId();

  /**
   * 前回リクエストの最終アクセス時間を取得する。
   *
   * @return 前回リクエストの最終アクセス時間
   */
  Date getLastAccessTime();

  /**
   * アプリケーションのコンテキストパスを取得する。
   *
   * @return コンテキストパス
   */
  String getContextPath();

  FWUser getUser();
}
