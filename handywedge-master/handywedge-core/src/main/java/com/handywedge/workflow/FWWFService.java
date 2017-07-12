/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.workflow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.handywedge.db.FWConnection;
import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWTransactional;

@ApplicationScoped
public class FWWFService {

  @Inject
  private FWConnectionManager cm;

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWWFStatus getStatus(String wfId) throws SQLException {
    FWConnection con = cm.getConnection();
    FWWFStatus status = null;
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT wfid.status_code, sm.status_name FROM fw_wf_id_management wfid"
            + " INNER JOIN fw_status_master AS sm ON wfid.status_code = sm.status"
            + " WHERE wfid.wf_id = ?")) {
      ps.setString(1, wfId);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          status = new FWWFStatus();
          status.setStatus(rs.getString(1));
          status.setStatusName(rs.getString(2));
        }
        return status;
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int insertWFIDManagement(FWWFIDManagement wfIdManagement) throws SQLException {
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(
        "INSERT INTO fw_wf_id_management (wf_id, status_code, create_date, update_date) VALUES(?, ?, ?, ?)")) {
      ps.setString(1, wfIdManagement.getWfId());
      ps.setString(2, wfIdManagement.getStatusCode());
      ps.setTimestamp(3, wfIdManagement.getCreateDate());
      ps.setTimestamp(4, wfIdManagement.getUpdateDate());
      return ps.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int updateWFIDManagement(FWWFIDManagement wfIdManagement) throws SQLException {
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(
        "UPDATE fw_wf_id_management SET status_code = ?, update_date = ? WHERE wf_id = ?")) {
      ps.setString(1, wfIdManagement.getStatusCode());
      ps.setTimestamp(2, wfIdManagement.getUpdateDate());
      ps.setString(3, wfIdManagement.getWfId());
      return ps.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWWFAction getRollbackAction(String wfId)
      throws SQLException {
    FWConnection con = cm.getConnection();
    FWWFAction action = null;
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT wfpm_pre.status_code, sm1.status_name, wfpm_pos.status_code, sm2.status_name FROM"
            + " (SELECT wf_id, max(wf_ser_no) AS wf_ser_no from fw_wf_progress_management WHERE wf_id = ? GROUP BY wf_id) AS st_base1"
            + " INNER JOIN (SELECT wf_id, wf_ser_no, row_number() over (order by wf_ser_no desc) AS odr from fw_wf_progress_management WHERE wf_id = ?) AS st_base2 ON st_base1.wf_id = st_base2.wf_id AND st_base2.odr = 2"
            + " INNER JOIN fw_wf_progress_management AS wfpm_pre ON st_base1.wf_id = wfpm_pre.wf_id AND st_base1.wf_ser_no = wfpm_pre.wf_ser_no"
            + " INNER JOIN fw_wf_progress_management AS wfpm_pos ON st_base2.wf_id = wfpm_pos.wf_id AND st_base2.wf_ser_no = wfpm_pos.wf_ser_no"
            + " INNER JOIN fw_status_master AS sm1 ON wfpm_pre.status_code = sm1.status"
            + " INNER JOIN fw_status_master AS sm2 ON wfpm_pos.status_code = sm2.status")) {
      ps.setString(1, wfId);
      ps.setString(2, wfId);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          action = new FWWFAction();
          action.setPreStatus(rs.getString(1));
          action.setPreStatusName(rs.getString(2));
          action.setPostStatus(rs.getString(3));
          action.setPostStatusName(rs.getString(4));
        }
        return action;
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public List<FWWFAction> getActions(String status, String role) throws SQLException {
    FWConnection con = cm.getConnection();
    List<FWWFAction> actions = new ArrayList<>();
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT wfrt.action_code, wfrt.action, wfrt.pre_status, sm1.status_name, wfrt.post_status, sm2.status_name, CASE WHEN wfrt2.pre_status IS NULL THEN 'true' ELSE 'false' END FROM fw_wf_rote AS wfrt"
            + " INNER JOIN fw_role_action AS ra ON wfrt.action_code = ra.action_code"
            + " LEFT JOIN fw_status_master AS sm1 ON wfrt.pre_status = sm1.status"
            + " LEFT JOIN fw_status_master AS sm2 ON wfrt.post_status = sm2.status"
            + " LEFT JOIN (SELECT pre_status FROM fw_wf_rote GROUP BY pre_status) AS wfrt2 ON wfrt.post_status = wfrt2.pre_status"
            + " WHERE wfrt.pre_status = ? AND ra.role = ?"
            + " ORDER BY wfrt.action_code ASC")) {
      ps.setString(1, status);
      ps.setString(2, role);
      try (FWResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          FWWFAction action = new FWWFAction();
          action.setActionCode(rs.getString(1));
          action.setAction(rs.getString(2));
          action.setPreStatus(rs.getString(3));
          action.setPreStatusName(rs.getString(4));
          action.setPostStatus(rs.getString(5));
          action.setPostStatusName(rs.getString(6));
          action.setLastAction(Boolean.valueOf(rs.getString(7)));
          actions.add(action);
        }
        return actions;
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWWFAction getAction(String actionCode, String role)
      throws SQLException {
    FWConnection con = cm.getConnection();
    FWWFAction action = null;
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT wfrt.action_code, wfrt.action, wfrt.pre_status, sm1.status_name, wfrt.post_status, sm2.status_name, CASE WHEN wfrt2.pre_status IS NULL THEN 'true' ELSE 'false' END FROM fw_wf_rote AS wfrt"
            + " INNER JOIN fw_role_action AS ra ON wfrt.action_code = ra.action_code"
            + " LEFT JOIN fw_status_master AS sm1 ON wfrt.pre_status = sm1.status"
            + " LEFT JOIN fw_status_master AS sm2 ON wfrt.post_status = sm2.status"
            + " LEFT JOIN (SELECT pre_status FROM fw_wf_rote GROUP BY pre_status) AS wfrt2 ON wfrt.post_status = wfrt2.pre_status"
            + " WHERE ra.role = ? AND wfrt.action_code = ?")) {
      ps.setString(1, role);
      ps.setString(2, actionCode);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          action = new FWWFAction();
          action.setActionCode(rs.getString(1));
          action.setAction(rs.getString(2));
          action.setPreStatus(rs.getString(3));
          action.setPreStatusName(rs.getString(4));
          action.setPostStatus(rs.getString(5));
          action.setPostStatusName(rs.getString(6));
          action.setLastAction(Boolean.valueOf(rs.getString(7)));
        }
        return action;
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWWFAction getAction(String actionCode, String status, String role)
      throws SQLException {
    FWConnection con = cm.getConnection();
    FWWFAction action = null;
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT wfrt.action_code, wfrt.action, wfrt.pre_status, sm1.status_name, wfrt.post_status, sm2.status_name, CASE WHEN wfrt2.pre_status IS NULL THEN 'true' ELSE 'false' END FROM fw_wf_rote AS wfrt"
            + " INNER JOIN fw_role_action AS ra ON wfrt.action_code = ra.action_code"
            + " LEFT JOIN fw_status_master AS sm1 ON wfrt.pre_status = sm1.status"
            + " LEFT JOIN fw_status_master AS sm2 ON wfrt.post_status = sm2.status"
            + " LEFT JOIN (SELECT pre_status FROM fw_wf_rote GROUP BY pre_status) AS wfrt2 ON wfrt.post_status = wfrt2.pre_status"
            + " WHERE wfrt.pre_status = ? AND ra.role = ? AND wfrt.action_code = ?")) {
      ps.setString(1, status);
      ps.setString(2, role);
      ps.setString(3, actionCode);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          action = new FWWFAction();
          action.setActionCode(rs.getString(1));
          action.setAction(rs.getString(2));
          action.setPreStatus(rs.getString(3));
          action.setPreStatusName(rs.getString(4));
          action.setPostStatus(rs.getString(5));
          action.setPostStatusName(rs.getString(6));
          action.setLastAction(Boolean.valueOf(rs.getString(7)));
        }
        return action;
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWWFLog insertWFLog(FWWFLog wfLog) throws SQLException {
    FWConnection con = cm.getConnection();
    // 履歴最大値取得
    int wfSerNo = 1;
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT MAX(wf_ser_no) FROM fw_wf_log"
            + " WHERE wf_id = ?")) {
      ps.setString(1, wfLog.getWfId());
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          wfSerNo = rs.getInt(1) + 1;
        }
      }
    }
    // 履歴登録処理
    try (FWPreparedStatement ps = con.prepareStatement(
        "insert into fw_wf_log (wf_id, wf_ser_no, action_date, action_owner, action_code, status_code, description) values(?, ?, ?, ?, ?, ?, ?)")) {
      ps.setString(1, wfLog.getWfId());
      ps.setInt(2, wfSerNo);
      ps.setTimestamp(3, wfLog.getActionDate());
      ps.setString(4, wfLog.getActionOwner());
      ps.setString(5, wfLog.getActionCode());
      ps.setString(6, wfLog.getStatusCode());
      ps.setString(7, wfLog.getDescription());
      ps.executeUpdate();
    }
    // 最新ログ取得
    return getWFLog(wfLog.getWfId(), wfSerNo);
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWWFLog getWFLog(String wfId, int wfSerNo) throws SQLException {
    FWConnection con = cm.getConnection();
    FWWFLog wfLog = null;
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT wflg.wf_id, wflg.wf_ser_no, wflg.action_date, wflg.action_owner, ur.name, wflg.action_code, wfrt.action, wflg.status_code, sm.status_name, wflg.description FROM fw_wf_log AS wflg"
            + " LEFT JOIN fw_user AS ur ON wflg.action_owner = ur.id"
            + " LEFT JOIN fw_wf_rote AS wfrt ON wflg.action_code = wfrt.action_code"
            + " LEFT JOIN fw_status_master AS sm ON wflg.status_code = sm.status"
            + " WHERE wflg.wf_id = ? AND wflg.wf_ser_no = ?")) {
      ps.setString(1, wfId);
      ps.setInt(2, wfSerNo);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          wfLog = new FWWFLog();
          wfLog.setWfId(rs.getString(1));
          wfLog.setWfSerNo(rs.getInt(2));
          wfLog.setActionDate(rs.getTimestamp(3));
          wfLog.setActionOwner(rs.getString(4));
          wfLog.setActionOwnerName(rs.getString(5));
          wfLog.setActionCode(rs.getString(6));
          wfLog.setActionName(rs.getString(7));
          wfLog.setStatusCode(rs.getString(8));
          wfLog.setStatusName(rs.getString(9));
          wfLog.setDescription(rs.getString(10));
        }
        return wfLog;
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public List<FWWFLog> getWFLogs(String wfId) throws SQLException {
    FWConnection con = cm.getConnection();
    List<FWWFLog> logs = new ArrayList<>();
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT wflg.wf_id, wflg.wf_ser_no, wflg.action_date, wflg.action_owner, ur.name, wflg.action_code, wfrt.action, wflg.status_code, sm.status_name, wflg.description FROM fw_wf_log AS wflg"
            + " LEFT JOIN fw_user AS ur ON wflg.action_owner = ur.id"
            + " LEFT JOIN fw_wf_rote AS wfrt ON wflg.action_code = wfrt.action_code"
            + " LEFT JOIN fw_status_master AS sm ON wflg.status_code = sm.status"
            + " WHERE wflg.wf_id = ?"
            + " ORDER BY wflg.wf_ser_no ASC")) {
      ps.setString(1, wfId);
      try (FWResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          FWWFLog wfLog = new FWWFLog();
          wfLog.setWfId(rs.getString(1));
          wfLog.setWfSerNo(rs.getInt(2));
          wfLog.setActionDate(rs.getTimestamp(3));
          wfLog.setActionOwner(rs.getString(4));
          wfLog.setActionOwnerName(rs.getString(5));
          wfLog.setActionCode(rs.getString(6));
          wfLog.setActionName(rs.getString(7));
          wfLog.setStatusCode(rs.getString(8));
          wfLog.setStatusName(rs.getString(9));
          wfLog.setDescription(rs.getString(10));
          logs.add(wfLog);
        }
        return logs;
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int insertWFProgressManagement(FWWFProgressManagement wfProgressManagement) throws SQLException {
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(
        "insert into fw_wf_progress_management (wf_id, wf_ser_no, action_code, status_code, create_date) values(?, ?, ?, ?, ?)")) {
      ps.setString(1, wfProgressManagement.getWfId());
      ps.setInt(2, wfProgressManagement.getWfSerNo());
      ps.setString(3, wfProgressManagement.getActionCode());
      ps.setString(4, wfProgressManagement.getStatusCode());
      ps.setTimestamp(5, wfProgressManagement.getCreateDate());
      return ps.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int deleteLastWFProgressManagement(String wfId) throws SQLException {
    FWConnection con = cm.getConnection();
    // 進捗管理TBLの最終レコード削除
    try (FWPreparedStatement ps = con.prepareStatement(
        "DELETE FROM fw_wf_progress_management"
            + " WHERE wf_id = ? AND wf_ser_no = (SELECT max(wf_ser_no) from fw_wf_progress_management WHERE wf_id = ?)")) {
      ps.setString(1, wfId);
      ps.setString(2, wfId);
      return ps.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int deleteWFProgressManagement(String wfId) throws SQLException {
    FWConnection con = cm.getConnection();
    // 進捗管理TBLの全レコード削除
    try (FWPreparedStatement ps = con.prepareStatement(
        "DELETE FROM fw_wf_progress_management"
            + " WHERE wf_id = ?")) {
      ps.setString(1, wfId);
      return ps.executeUpdate();
    }
  }
}
