package com.csframe.context;

import java.util.Date;
import java.util.Map;

import com.csframe.user.FWFullUser;

/**
 * FW内部のみ使用します。
 */
public interface FWFullContext extends FWContext {

  /**
   * リクエストIDを設定する。
   *
   * @param requestId リクエストID
   */
  void setRequestId(String requestId);

  /**
   * リクエスト開始時刻を設定する。
   *
   * @param requestStartTime リクエスト開始時刻
   */
  void setRequestStartTime(Date requestStartTime);

  /**
   * 実行中のサーバーの名称を設定する。
   *
   * @param hostName 実行中のサーバーの名称
   */
  void setHostName(String hostName);

  /**
   * アプリケーションIDを設定する。
   *
   * @param applicationId アプリケーションID
   */
  void setApplicationId(String applicationId);

  /**
   * 最終アクセス時間を設定する。 このメソッドはFWFilterのdoFilterメソッドの最後で呼び出される。
   *
   * @param lastAccessTime 最終アクセス時間
   */
  void setLastAccessTime(Date lastAccessTime);

  /**
   * アプリケーションのコンテキストパスを設定する。
   *
   * @param contextPath コンテキストパス
   */
  void setContextPath(String contextPath);

  void setAPITokenAuth(boolean apiToken);

  void setUser(FWFullUser user);

  Map<String, String> getTokenMap();

}
