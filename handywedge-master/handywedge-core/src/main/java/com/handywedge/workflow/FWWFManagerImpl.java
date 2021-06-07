/*
 * Copyright (c) 2019 Handywedge Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.workflow;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.context.FWFullContext;
import com.handywedge.log.FWLogger;

@ApplicationScoped
public class FWWFManagerImpl implements FWWFManager {

  private static final String ROLLBACK_ACTION_CODE = "ROLLBACK_ACTION";
  private static final String ROLLBACK_ACTION_NAME = "否認";

  @Inject
  private FWLogger logger;

  @Inject
  private FWFullContext ctx;

  @Inject
  private FWWFService service;

  @Override
  public FWWFStatus getStatus(String wfId) {
    long startTime = logger.perfStart("getStatus");
    try {
      FWWFStatus status = null;
      if (wfId != null) {
        status = service.getStatus(wfId);
      }
      logger.debug("wfId={}", wfId);
      logger.perfEnd("getStatus", startTime);
      return status;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public FWWFAction getAction(String actionCode) {
    long startTime = logger.perfStart("getAction");
    FWWFAction action = getAction(actionCode, ctx.getUser().getRole());
    logger.perfEnd("getAction", startTime);
    return action;
  }

  @Override
  public FWWFAction getActionInfo(String actionCode, String role) {
    long startTime = logger.perfStart("getActionInfo");
    FWWFAction action = getAction(actionCode, role);
    logger.perfEnd("getActionInfo", startTime);
    return action;
  }

  private FWWFAction getAction(String actionCode, String role) {
    try {
      // 実行可能なアクション取得
      FWWFAction action = null;
      if (role != null) {
        action = service.getAction(actionCode, role);
      }
      logger.debug("role={}, actionCode={}", role, actionCode);
      return action;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public List<FWWFAction> getActions(String wfId) {
    long startTime = logger.perfStart("getActions");
    List<FWWFAction> actions = getActions(wfId, ctx.getUser().getRole());
    logger.perfEnd("getActions", startTime);
    return actions;
  }

  @Override
  public List<FWWFAction> getActionInfos(String wfId, String role) {
    long startTime = logger.perfStart("getActionInfos");
    List<FWWFAction> actions = getActions(wfId, role);
    logger.perfEnd("getActionInfos", startTime);
    return actions;
  }

  private List<FWWFAction> getActions(String wfId, String role) {
    try {
      // 現在ステータス取得
      FWWFStatus status = null;
      if (wfId != null) {
        status = service.getStatus(wfId);
      }
      // 実行可能なアクションリスト取得
      List<FWWFAction> actions = new ArrayList<>();
      if (status != null && role != null) {
        actions = service.getActions(status.getStatus(), role);
      }
      // RollBackアクション生成
      if (wfId != null && !actions.isEmpty()) {
        // 実行可能なアクションが存在することにより現在ステータスより前に戻すことが可能なロールであると判断
        // Rollbackアクション生成
        FWWFAction rollbackAction = service.getRollbackAction(wfId);
        if (rollbackAction != null) {
          rollbackAction.setWfId(wfId);
          rollbackAction.setActionCode(ROLLBACK_ACTION_CODE);
          rollbackAction.setAction(ROLLBACK_ACTION_NAME);
          rollbackAction.setRollbackAction(true);
          actions.add(rollbackAction);
        }
      }
      // ワークフローIDのセット
      actions.forEach(action -> action.setWfId(wfId));
      logger.debug("role={}, wfId={}", role, wfId);
      return actions;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public List<FWWFAction> getGoActions(String wfId) {
    long startTime = logger.perfStart("getGoActions");
    List<FWWFAction> actions = getGoActions(wfId, ctx.getUser().getRole());
    logger.perfEnd("getGoActions", startTime);
    return actions;
  }

  @Override
  public List<FWWFAction> getGoActionInfos(String wfId, String role) {
    long startTime = logger.perfStart("getGoActionInfos");
    List<FWWFAction> actions = getGoActions(wfId, role);
    logger.perfEnd("getGoActionInfos", startTime);
    return actions;
  }

  private List<FWWFAction> getGoActions(String wfId, String role) {
    try {
      // 現在ステータス取得
      FWWFStatus status = null;
      if (wfId != null) {
        status = service.getStatus(wfId);
      }
      // 実行可能なアクションリスト取得
      List<FWWFAction> actions = new ArrayList<>();
      if (status != null && role != null) {
        actions = service.getActions(status.getStatus(), role);
      }
      // ワークフローIDのセット
      actions.forEach(action -> action.setWfId(wfId));
      logger.debug("role={}, wfId={}", role, wfId);
      return actions;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public FWWFAction getRollBackAction(String wfId) {
    long startTime = logger.perfStart("getRollBackAction");
    FWWFAction action = getRollBackAction(wfId, ctx.getUser().getRole());
    logger.perfEnd("getRollBackAction", startTime);
    return action;
  }

  @Override
  public FWWFAction getRollBackActionInfo(String wfId, String role) {
    long startTime = logger.perfStart("getRollBackActionInfo");
    FWWFAction action = getRollBackAction(wfId, ctx.getUser().getRole());
    logger.perfEnd("getRollBackActionInfo", startTime);
    return action;
  }

  private FWWFAction getRollBackAction(String wfId, String role) {
    try {
      FWWFAction action = generateRollbackAction(wfId, ROLLBACK_ACTION_CODE, role);
      logger.debug("role={}, wfId={}, actionCode={}", role, wfId, ROLLBACK_ACTION_CODE);
      return action;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public FWWFAction checkAction(String wfId, String actionCode) throws FWWFException {
    long startTime = logger.perfStart("checkAction");
    try {
      FWWFAction action = generateActionWithStatusCheck(wfId, actionCode, ctx.getUser().getRole());
      if (action == null) {
        logger.info("unauthorized. role={}, wfId={}, actionCode={}", ctx.getUser().getRole(), wfId,
            actionCode);
        throw new FWWFException(FWConstantCode.ROLE_UNAUTHORIZED);
      }
      logger.debug("role={}, wfId={}, actionCode={}", ctx.getUser().getRole(), wfId, actionCode);
      logger.perfEnd("checkAction", startTime);
      return action;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public FWWFLog doInitAction(String actionCode) throws FWWFException {
    FWWFAction action = new FWWFAction();
    action.setActionCode(actionCode);
    return doInitAction(action, null);
  }

  @Override
  public FWWFLog doInitAction(String actionCode, String description) throws FWWFException {
    FWWFAction action = new FWWFAction();
    action.setActionCode(actionCode);
    return doInitAction(action, description);
  }

  @Override
  public FWWFLog doInitAction(FWWFAction wfAction) throws FWWFException {
    return doInitAction(wfAction, null);
  }

  @Override
  public FWWFLog doInitAction(FWWFAction wfAction, String description) throws FWWFException {
    long startTime = logger.perfStart("doInitAction");
    try {
      String role = ctx.getUser().getRole();
      String actionCode = wfAction.getActionCode();
      FWWFAction action = generateAction(null, actionCode, role);
      if (action == null) {
        logger.info("unauthorized. role={}, actionCode={}", role, actionCode);
        throw new FWWFException(FWConstantCode.ROLE_UNAUTHORIZED);
      }
      // ワークフローIDの生成
      String wfId = UUID.randomUUID().toString();
      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
      // ワークフローID管理TBL
      FWWFIDManagement wfIdManagement = new FWWFIDManagement();
      wfIdManagement.setWfId(wfId);
      wfIdManagement.setStatusCode(action.getPostStatus());
      wfIdManagement.setCreateDate(currentTime);
      wfIdManagement.setUpdateDate(currentTime);
      service.insertWFIDManagement(wfIdManagement);
      // ワークフローログTBL
      FWWFLog wfLog = new FWWFLog();
      wfLog.setWfId(wfId);
      wfLog.setActionDate(currentTime);
      wfLog.setActionOwner(ctx.getUser().getId());
      wfLog.setActionCode(action.getActionCode());
      wfLog.setStatusCode(action.getPostStatus());
      wfLog.setDescription(description);
      wfLog = service.insertWFLog(wfLog);
      // ワークフロー進捗管理TBL
      FWWFProgressManagement wfProgressManagement = new FWWFProgressManagement();
      wfProgressManagement.setWfId(wfId);
      wfProgressManagement.setWfSerNo(wfLog.getWfSerNo());
      wfProgressManagement.setActionCode(action.getActionCode());
      wfProgressManagement.setStatusCode(action.getPostStatus());
      wfProgressManagement.setCreateDate(currentTime);
      service.insertWFProgressManagement(wfProgressManagement);

      logger.debug("role={}, wfId={}, actionCode={}", role, wfId, actionCode);
      logger.perfEnd("doInitAction", startTime);
      return wfLog;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public FWWFLog doAction(String wfId, String actionCode) throws FWWFException {
    FWWFAction action = new FWWFAction();
    action.setWfId(wfId);
    action.setAction(actionCode);
    return doAction(action, null);
  }

  @Override
  public FWWFLog doAction(String wfId, String actionCode, String description) throws FWWFException {
    FWWFAction action = new FWWFAction();
    action.setWfId(wfId);
    action.setAction(actionCode);
    return doAction(action, description);
  }

  @Override
  public FWWFLog doAction(FWWFAction wfAction) throws FWWFException {
    return doAction(wfAction, null);
  }

  @Override
  public FWWFLog doAction(FWWFAction wfAction, String description) throws FWWFException {
    long startTime = logger.perfStart("doAction");
    try {
      String role = ctx.getUser().getRole();
      String actionCode = wfAction.getActionCode();
      String wfId = wfAction.getWfId();
      FWWFAction action = generateAction(wfId, actionCode, role);
      if (action == null) {
        logger.info("unauthorized. role={}, actionCode={}", role, actionCode);
        throw new FWWFException(FWConstantCode.ROLE_UNAUTHORIZED);
      }
      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
      // ワークフローID管理TBL
      FWWFIDManagement wfIdManagement = new FWWFIDManagement();
      wfIdManagement.setWfId(wfId);
      wfIdManagement.setStatusCode(action.getPostStatus());
      wfIdManagement.setUpdateDate(currentTime);
      service.updateWFIDManagement(wfIdManagement);
      // ワークフローログTBL
      FWWFLog wfLog = new FWWFLog();
      wfLog.setWfId(wfId);
      wfLog.setActionDate(currentTime);
      wfLog.setActionOwner(ctx.getUser().getId());
      wfLog.setActionCode(action.getActionCode());
      wfLog.setStatusCode(action.getPostStatus());
      wfLog.setDescription(description);
      wfLog = service.insertWFLog(wfLog);
      // ワークフロー進捗管理TBL
      if (actionCode.equals(ROLLBACK_ACTION_CODE)) {
        // ロールバックアクションの場合、最終履歴の削除
        service.deleteLastWFProgressManagement(wfId);
      } else if (action.isLastAction()) {
        // 承認系アクション（最終）の場合、WFIDに紐付くワークフロー進捗管理TBLのレコードを削除
        service.deleteWFProgressManagement(wfId);
      } else {
        // 承認系アクション（通常）の場合
        FWWFProgressManagement wfProgressManagement = new FWWFProgressManagement();
        wfProgressManagement.setWfId(wfId);
        wfProgressManagement.setWfSerNo(wfLog.getWfSerNo());
        wfProgressManagement.setActionCode(action.getActionCode());
        wfProgressManagement.setStatusCode(action.getPostStatus());
        wfProgressManagement.setCreateDate(currentTime);
        service.insertWFProgressManagement(wfProgressManagement);
      }

      logger.debug("role={}, wfId={}, actionCode={}", role, wfId, actionCode);
      logger.perfEnd("doAction", startTime);
      return wfLog;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  @Override
  public List<FWWFLog> getWFLogs(String wfId) {
    long startTime = logger.perfStart("getWFLogs");
    try {
      List<FWWFLog> logs = new ArrayList<>();
      if (wfId != null) {
        logs = service.getWFLogs(wfId);
        logs.forEach(log -> {
          if (log.getActionCode().equals(ROLLBACK_ACTION_CODE)) {
            log.setActionName(ROLLBACK_ACTION_NAME);
          }
        });
      }
      logger.debug("wfId={}", wfId);
      logger.perfEnd("getWFLogs", startTime);
      return logs;
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
  }

  private FWWFAction generateAction(String wfId, String actionCode, String role)
      throws SQLException {
    // 該当するアクションの取得
    FWWFAction action = null;
    if (actionCode != null) {
      boolean rollbackFlg = actionCode.equals(ROLLBACK_ACTION_CODE);
      if (!rollbackFlg) {
        // 承認系アクション(現在ステータスとの整合性チェックなし)
        action = service.getAction(actionCode, role);
        action.setWfId(wfId);
      } else {
        // 否認系アクション（ロールバック）
        action = generateRollbackAction(wfId, actionCode, role);
      }
    }
    return action;
  }

  private FWWFAction generateActionWithStatusCheck(String wfId, String actionCode, String role)
      throws SQLException {
    // 該当するアクションの取得
    FWWFAction action = null;
    if (actionCode != null) {
      boolean rollbackFlg = actionCode.equals(ROLLBACK_ACTION_CODE);
      if (!rollbackFlg) {
        // 承認系アクション（現在ステータスとの整合性チェックあり）
        FWWFStatus status = null;
        if (wfId != null) {
          // 現在ステータス取得
          status = service.getStatus(wfId);
          // アクションコード、ロールに加え、ステータスの突合を行う場合（＝現在ステータスとの整合性チェック）
          if (status != null) {
              action = service.getAction(actionCode, status.getStatus(), role);
              action.setWfId(wfId);
          }
        }
      } else {
        // 否認系アクション（ロールバック）
        action = generateRollbackAction(wfId, actionCode, role);
      }
    }
    return action;
  }

  private FWWFAction generateRollbackAction(String wfId, String actionCode, String role)
      throws SQLException {
    FWWFAction action = null;
    // 現在ステータス取得
    FWWFStatus status = null;
    if (wfId != null) {
      status = service.getStatus(wfId);
    }
    // 否認系アクション（ロールバック）
    if (status != null) {
      // ステータス有り＝チェック：実行可能なアクション取得。存在することにより現在ステータスより前に戻すことが可能なロールであると判断
      if (service.getActions(status.getStatus(), role).isEmpty()) {
        return null;
      }
    }
    // Rollbackアクション生成
    action = service.getRollbackAction(wfId);
    if (action != null) {
      action.setWfId(wfId);
      action.setActionCode(ROLLBACK_ACTION_CODE);
      action.setAction(ROLLBACK_ACTION_NAME);
      action.setRollbackAction(true);
    }
    return action;
  }

}
