package com.csframe.context;

import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import com.csframe.user.FWFullUser;

import lombok.Data;

/**
 * FW内部のみ使用。
 */
@Data
@SessionScoped
public class FWSessionContextImpl implements FWSessionContext {

  private static final long serialVersionUID = 1L;

  private Date lastAccessTime;

  @Inject
  private FWFullUser user;

}
