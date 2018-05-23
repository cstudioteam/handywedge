/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.config;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * プロパティファイルの値を取得するためのインターフェースです。<br>
 * プロパティファイルの基底名はアプリケーションIDを設定します。<br>
 * 例えばアプリケーションIDが「handywedge-test-app」の場合はhandywedge-test-app.properties、handywedge-test-app_ja.propertiesなどです。<br>
 * プロパティファイルはルートに配置します。
 */
public interface FWMessageResources {

  String LOGIN_URL = "fw.login.url";
  String REGISTER_URL = "fw.register.url";
  String PRE_REGISTER_URL = "fw.pre_register.url";
  String CRYPTO_KEY = "fw.crypto.key";
  String CRYPTO_IV = "fw.crypto.iv";
  String SESSION_COOKIE_NAME = "fw.session.cookie.name";
  String REGISTER_FROM_ADDR = "fw.user.register.notice.from.address";
  String PRE_REGISTER_SUBJECT = "fw.pre.user.register.notice.subject";
  String PASSWD_RESET_SUBJECT = "fw.user.register.notice.subject";
  String ACT_REGISTER_SUCCESS_REDIRECT = "fw.pre.user.register.success.redirect";
  String ACT_REGISTER_FAIL_REDIRECT = "fw.pre.user.register.fail.redirect";
  String RESET_PASSWD_SUCCESS_REDIRECT = "fw.user.register.passwd.reset.success.redirect";
  String RESET_PASSWD_FAIL_REDIRECT = "fw.user.register.passwd.reset.fail.redirect";
  String SERVER_ADDR = "fw.server.address";
  String IGNORE_AUTH_URL = "fw.ignore.auth.url";
  String TOKEN_TIMEOUT_SEC = "fw.token.timeout.sec";

  /**
   * プロパティファイルに設定された値を取得します。<br>
   * プロパティファイルにキーが設定されていない場合はnullが返されます。<br>
   * ロケールはログインしているユーザーに設定されたロケールが使用されます。<br>
   *
   * @param key 取得するキー
   * @return 取得された値
   */
  String get(String key);

  /**
   * プロパティファイルに設定された値を取得します。<br>
   * プロパティファイルにキーが設定されていない場合はnullが返されます。<br>
   * ロケールは引数localeが使用されます。<br>
   *
   * @param key 取得するキー
   * @param locale ロケール
   * @return 取得された値
   */
  String get(String key, Locale locale);

  /**
   * すべてのキーのSetを返します。<br>
   * ロケールはログインしているユーザーに設定されたロケールが使用されます。<br>
   *
   * @return すべてのキーのSet
   */
  Set<String> keySet();

  /**
   * すべてのキーのSetを返します。<br>
   * ロケールは引数localeが使用されます。<br>
   *
   * @param locale ロケール
   * @return すべてのキーのSet
   */
  Set<String> keySet(Locale locale);

  /**
   * ログインしているユーザーで現在使用されるResourceBundleインスタンスを返します。
   *
   * @return ResourceBundleインスタンス
   */
  ResourceBundle getBundle();

  /**
   * 指定したlocaleで現在使用されるResourceBundleインスタンスを返します。
   *
   * @param locale ロケール
   * @return ResourceBundleインスタンス
   */
  ResourceBundle getBundle(Locale locale);
}
