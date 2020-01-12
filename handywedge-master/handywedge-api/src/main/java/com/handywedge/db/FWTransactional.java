/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = {ElementType.TYPE, ElementType.METHOD})
/**
 * DBへアクセスするトランザクション開始点となるメソッドに宣言します。<br>
 * 当アノテーションを宣言しないとコネクションが生成されないため、参照のみの場合でも宣言が必要になります。<br>
 * 
 * @see javax.transaction.Transactional
 */
public @interface FWTransactional {

  @Nonbinding
  /**
   * トランザクションタイプを指定します。<br>
   * 現在のバージョンではREQUIREDのみサポートされます。
   * 
   * @return トランザクションタイプ
   */
  public FWTransactional.FWTxType value() default FWTxType.REQUIRED;

  /* 例外は基本的に全てロールバックさせ、例外的にロールバックさせたくないものをdontRollbackOnで指定させる */
  // @Nonbinding
  // public Class<? extends Exception>[] rollbackOn() default {};

  @Nonbinding
  /**
   * トランザクション内で例外が発生した場合はインターセプターによりロールバックされますが、ここで指定した例外クラスがスローされた場合はロールバックを行いません。<br>
   * JTA仕様ではチェック例外が発生した場合はロールバックされませんが、handywedgeでは指定しないかぎりは<b>全ての例外</b>がロールバックされるので注意して下さい。<br>
   * 
   * @return ロールバックさせない例外クラス配列
   */
  public Class<? extends Exception>[] dontRollbackOn() default {};

  @Nonbinding
  /**
   * DB接続に使用するデータソースを指定します。<br>
   * web.xmlに指定したres-ref-nameを指定して下さい<br>
   * 
   * @return ルックアップするデータソース名
   */
  public String dataSourceName() default "jdbc/ds";

  /**
   * トランザクションタイプを定義します。
   */
  public enum FWTxType {

    /**
     * トランザクションを新しく開始します。<br>
     * 既にトランザクションが開始されていた場合は、そのトランザクション内で処理が行われます。<br>
     */
    REQUIRED;
  }

}
