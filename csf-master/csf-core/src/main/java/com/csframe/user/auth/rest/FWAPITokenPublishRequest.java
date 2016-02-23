package com.csframe.user.auth.rest;

import lombok.Data;

@Data
public class FWAPITokenPublishRequest {

  private String id;
  private String password;

}
