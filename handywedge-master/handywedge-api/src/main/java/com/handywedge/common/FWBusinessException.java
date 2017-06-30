/*
 * Copyright (c) 2016-2017 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * アプリケーションが意図的に例外を発生させたい時に、例外クラスに当アノテーションを使用します。
 * フレームワークで例外をキャッチした場合はWARNレベルでエラーログにスタックトレース情報が出力されます。
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FWBusinessException {

}
