/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.common.FWStringUtil;
import com.handywedge.context.FWFullContext;
import com.handywedge.log.FWLogger;

@ApplicationScoped
public class FWRoleManagerImpl implements FWRoleManager {

  @Inject
  private FWLogger logger;

  @Inject
  private FWFullContext ctx;

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
  public FWAction checkAction(String preStatus, String postStatus) throws FWRoleException {
    return checkAction(preStatus, postStatus, ctx.getUser().getRole());
  }

  @Override
  public FWAction checkAction(String preStatus, String postStatus, String role)
      throws FWRoleException {
    long startTime = logger.perfStart("checkAction");
    try {
      FWAction actionCode = null;
      if (role != null) {
        actionCode = service.getActionCode(preStatus, postStatus, role);
      }
      if (actionCode == null) {
        logger.info("unauthorized. role={}, preStatus={}, postStatus={}", role, preStatus,
            postStatus);
        throw new FWRoleException(FWConstantCode.ROLE_UNAUTHORIZED);
      }
      logger.perfEnd("checkAction", startTime);
      return actionCode;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public boolean isAccessAllow() {
    long startTime = logger.perfStart("isAccessAllow");

    List<FWRoleAcl> acl = ctx.getRoleAcl();
    if (acl.isEmpty()) { // ロール設定が無い場合はロールACL機能は未使用とみなす
      return true;
    }
    if (FWStringUtil.isEmpty(ctx.getUser().getRole())) { // ロールACLの設定はあるがユーザーにロール設定がされていない場合はfalseとする
      return false;
    }
    // ロール別にグルーピングしてログインユーザのロールのURLを抽出
    List<FWRoleAcl> grouping =
        acl.stream().collect(Collectors.groupingBy(FWRoleAcl::getRole))
            .get(ctx.getUser().getRole());
    if (grouping == null) {
      return false;
    }
    boolean result = false;
    for (FWRoleAcl roleAcl : grouping) {
      result = roleAcl.getPattern().matcher(ctx.getRequestUrl()).matches();
      if (result) {
        logger.debug("URLパターンマッチ role={}, pattern={}, url={}", roleAcl.getRole(),
            roleAcl.getUrlPattern(), ctx.getRequestUrl());
        break;
      }
    }

    logger.perfEnd("isAccessAllow", startTime);
    return result;
  }
}
