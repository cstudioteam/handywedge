/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.context;

import java.util.Date;

import javax.enterprise.context.RequestScoped;

import lombok.Data;

@Data
@RequestScoped
public class FWRequestContextImpl implements FWRequestContext {

  private String requestId;
  private String contextPath;
  private Date requestStartTime;
  private boolean rest;
  private String token;
  private String requestUrl;
  private String preToken;

}
