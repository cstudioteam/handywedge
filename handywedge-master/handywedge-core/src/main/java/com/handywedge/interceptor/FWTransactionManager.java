/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.interceptor;

import jakarta.enterprise.context.RequestScoped;

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
