/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.context;

import java.util.Date;

import javax.enterprise.context.RequestScoped;

import com.handywedge.context.FWRequestContext;

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
