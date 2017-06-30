/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * クライアントから送信されたJSONをアンマーシャルするためのクラスです。
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = {ElementType.METHOD})
public @interface FWRESTRequestClass {

  Class<? extends FWRESTRequest> value();

}
