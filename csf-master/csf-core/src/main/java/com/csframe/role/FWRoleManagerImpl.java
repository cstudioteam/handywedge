/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.csframe.role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.csframe.common.FWConstantCode;
import com.csframe.common.FWRuntimeException;
import com.csframe.context.FWContext;
import com.csframe.log.FWLogger;

// !!実装中
@ApplicationScoped
public class FWRoleManagerImpl implements FWRoleManager {

  @Inject
  private FWLogger logger;

  @Inject
  private FWContext ctx;

  @Inject
  private FWRoleService service;

  @Override
  public List<String> getActions(String currentStatus) {
    return getActions(currentStatus, ctx.getUser().getRole());
  }

  @Override
  public List<String> getActions(String currentStatus, String role) {

    long startTime = logger.perfStart("getActions");
    try {
      List<String> actions = new ArrayList<>();
      if (role != null) {
        actions = service.getActions(currentStatus, role);
      }
      logger.debug("role={}, actions={}", role, actions);
      logger.perfEnd("getActions", startTime);
      return actions;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public String checkAction(String preStatus, String postStatus) {
    return checkAction(preStatus, postStatus, ctx.getUser().getRole());
  }

  @Override
  public String checkAction(String preStatus, String postStatus, String role) {
    long startTime = logger.perfStart("checkAction");
    try {
      String actionCode = null;
      if (role != null) {
        actionCode = service.getActionCode(preStatus, postStatus, role);
      }
      logger.perfEnd("checkAction", startTime);
      return actionCode;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }
}
