/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.cdi;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * &#64;Injectでのインスタンスが取得できない場合に直接BeanManagerを介してCDI管理インスタンスを取得するためのクラスです。<br>
 * 以下のようにしてインスタンスを取得します。<br>
 * 
 * <pre>
 * 
 * {@code    @Inject}
 * {@code
 *     FWMessageResources resources = FWBeanManager.getBean(FWMessageResources.class);
 * }
 * 
 * </pre>
 */
public class FWBeanManager {

  private static BeanManager beanManager;

  @SuppressWarnings("unchecked")
  public static <T> T getBean(Class<T> beanType, Annotation... bindings) {

    BeanManager manager = getBeanManager(false);
    if (manager == null) {
      return null;
    }
    Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(beanType, bindings));
    if (bean == null) {
      manager = getBeanManager(true);
      bean = (Bean<T>) manager.resolve(manager.getBeans(beanType, bindings));
      if (bean == null) {
        return null;
      }
    }
    return (T) manager.getReference(bean, beanType, manager.createCreationalContext(bean));
  }

  private static BeanManager getBeanManager(boolean isRefresh) {

    if (beanManager == null || isRefresh) {
      try {
        beanManager = InitialContext.doLookup("java:comp/env/BeanManager");
      } catch (NamingException e) {
        // TODO 例外処理
        e.printStackTrace();
      }
    }

    return beanManager;
  }

}
