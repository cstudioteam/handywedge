package com.csframe.context;

import javax.enterprise.context.ApplicationScoped;

import com.csframe.context.FWApplicationContext;

import lombok.Data;

@Data
@ApplicationScoped
public class FWApplicationContextImpl implements FWApplicationContext {

  private String hostName;
  private String applicationId;

}
