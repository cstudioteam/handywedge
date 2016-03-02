package com.csframe.rest;

import javax.enterprise.context.RequestScoped;

import com.csframe.common.FWConstantCode;

// インターフェースのdefault実装ではなく抽象クラスとする
@RequestScoped
public abstract class FWRESTController {

  @FWRESTRequestClass(FWRESTEmptyRequest.class)
  public FWRESTResponse doPost(FWRESTRequest request) {
    return createError();
  }

  public FWRESTResponse doGet(String param) {
    return createError();
  }

  @FWRESTRequestClass(FWRESTEmptyRequest.class)
  public FWRESTResponse doPut(FWRESTRequest request) {
    return createError();
  }

  @FWRESTRequestClass(FWRESTEmptyRequest.class)
  public FWRESTResponse doDelete(FWRESTRequest request) {
    return createError();
  }

  private FWRESTResponse createError() {
    FWRESTResponse res = new FWRESTErrorResponse();
    res.setReturn_cd(FWConstantCode.FW_REST_UNSUPPORTED);
    res.setReturn_msg("未実装のメソッドが呼ばれました。");
    return res;
  }
}
