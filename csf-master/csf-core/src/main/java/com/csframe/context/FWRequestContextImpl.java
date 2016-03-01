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
  private boolean APITokenAuth;

}
