package com.csframe.user.auth.rest;

import lombok.Data;

@Data
public class FWAPITokenRequest {

  private String id;
  private String password;

}
