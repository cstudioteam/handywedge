package com.csframe.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * フレームワーク専用クラス。 Contextで持ちまわれない情報を管理するためThreadLocalを利用する。
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class FWThreadLocal {

  public static final String INTERCEPTOR_ERROR = "csf.interceptor.error";
  public static final String LOGIN = "csf.login";

  private static final ThreadLocal<ConcurrentHashMap> instance =
      new ThreadLocal<ConcurrentHashMap>() {
        @Override
        protected synchronized ConcurrentHashMap initialValue() {

          return new ConcurrentHashMap();
        }
      };

  /**
   * ThreadLocalに任意のオブジェクトを保存する。
   *
   * @param <V> 保存するオブジェクトの型
   * @param key 保存する際のキー
   * @param value 保存するオブジェクト
   * @return 以前にキーと関連付けられていた値
   */

  public static <V> V put(String key, V value) {

    return (V) instance.get().put(key, value);
  }

  /**
   * ThreadLocalに保存したオブジェクトを取得する。
   *
   * @param <V> 取得するオブジェクトの型
   * @param key 取得するオブジェクトと関連付けられたキー
   * @return キーに関連付けられたオブジェクト
   */
  public static <V> V get(String key) {

    return (V) instance.get().get(key);
  }

  public static <V> V remove(String key) {

    return (V) instance.get().remove(key);
  }

  /**
   * 保持している全ての値をクリアし、ThreadLocalをremove()する。
   */
  public static void destroy() {

    try {
      instance.get().clear();
    } catch (Exception e) {
      // ignore
    } finally {
      instance.remove();
    }
  }
}
