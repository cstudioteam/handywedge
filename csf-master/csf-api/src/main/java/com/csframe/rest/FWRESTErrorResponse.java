package com.csframe.rest;

public class FWRESTErrorResponse extends FWRESTResponse {

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("FWRESTErrorResponse [getReturnCd()=").append(getReturn_cd())
        .append(", getReturnMsg()=").append(getReturn_msg()).append("]");
    return builder.toString();
  }

}
