package jp.cstudio.csfw.interceptor;

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
