package com.csframe.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FWRESTEmptyRequest extends FWRESTRequest {

  @Override
  public String toString() {
    return "FWRESTEmptyRequest class.";
  }

}
