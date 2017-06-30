/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.interceptor;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class FWTransactionManager {

  private int layer = 0;

  boolean isTopLayer() {

    return layer == 0;
  }

  void incrementLayer() {

    layer++;
  }

  void decrementLayer() {

    layer--;
  }

}
