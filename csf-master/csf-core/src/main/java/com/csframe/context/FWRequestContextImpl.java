package com.csframe.context;

import java.util.Date;

import javax.enterprise.context.RequestScoped;

import com.csframe.context.FWRequestContext;

import lombok.Data;

@Data
@RequestScoped
public class FWRequestContextImpl implements FWRequestContext {

  private String requestId;
  private String contextPath;
  private Date requestStartTime;

}
