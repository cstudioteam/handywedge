package com.csframe.context;

import javax.enterprise.context.ApplicationScoped;

import lombok.Data;

@Data
@ApplicationScoped
public class FWApplicationContextImpl implements FWApplicationContext {

  private String hostName;
  private String applicationId;
  private String contextPath;

}
