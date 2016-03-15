/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.common;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.csframe.cdi.FWBeanManager;
import com.csframe.context.FWSessionContext;

/**
 * csFrameの例外クラスがスローされた時にエラーメッセージを生成するためのクラスです。<br>
 * com.csframe.common.fw_error_messages.propertiesからエラーメッセージを取得します<br>
 */
public class FWErrorMessage {

  static String getMessage(String code, Object... args) {

    Locale locale;
    try {
      FWSessionContext ctx = FWBeanManager.getBean(FWSessionContext.class);
      locale = ctx.getUser().getLocale();
    } catch (Exception e) {
      // コンテキストリスナーなどセッションスコープに入る前はエラーとなる
      locale = Locale.getDefault();
    }
    ResourceBundle rb = ResourceBundle.getBundle("com.csframe.common.fw_error_messages", locale);
    String msg = rb.getString(code);
    if (args != null) {
      msg = MessageFormat.format(msg, args);
    }
    return "[" + code + "] " + msg;
  }
}
