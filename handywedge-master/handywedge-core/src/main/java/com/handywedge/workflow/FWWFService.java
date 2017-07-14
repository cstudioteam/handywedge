/*
 * Copyright (c) 2016ｰ2017 C Studio Co.,Ltd.
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
        "SELECT wfid.status_code AS status_code, sm.status_name AS status_name FROM fw_wf_id_management wfid"
            + " INNER JOIN fw_status_master AS sm ON wfid.status_code = sm.status"
            + " WHERE wfid.wf_id = ?")) {
      ps.setString(1, wfId);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          status = new FWWFStatus();
          status.setStatus(rs.getString("status_code"));
          status.setStatusName(rs.getString("status_name"));
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
      int idx = 1;
      ps.setString(idx++, wfIdManagement.getWfId());
      ps.setString(idx++, wfIdManagement.getStatusCode());
      ps.setTimestamp(idx++, wfIdManagement.getCreateDate());
      ps.setTimestamp(idx++, wfIdManagement.getUpdateDate());
      return ps.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public int updateWFIDManagement(FWWFIDManagement wfIdManagement) throws SQLException {
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(
        "UPDATE fw_wf_id_management SET status_code = ?, update_date = ? WHERE wf_id = ?")) {
      int idx = 1;
      ps.setString(idx++, wfIdManagement.getStatusCode());
      ps.setTimestamp(idx++, wfIdManagement.getUpdateDate());
      ps.setString(idx++, wfIdManagement.getWfId());
      return ps.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWWFAction getRollbackAction(String wfId)
      throws SQLException {
    FWConnection con = cm.getConnection();
    FWWFAction action = null;
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT wfpm_pre.status_code AS pre_status_code, sm1.status_name AS pre_status_name, wfpm_pos.status_code AS post_status_code, sm2.status_name AS post_status_name FROM"
            + " (SELECT wf_id, max(wf_ser_no) AS wf_ser_no from fw_wf_progress_management WHERE wf_id = ? GROUP BY wf_id) AS st_base1"
            + " INNER JOIN (SELECT wf_id, wf_ser_no, row_number() over (order by wf_ser_no desc) AS odr from fw_wf_progress_management WHERE wf_id = ?) AS st_base2 ON st_base1.wf_id = st_base2.wf_id AND st_base2.odr = 2"
            + " INNER JOIN fw_wf_progress_management AS wfpm_pre ON st_base1.wf_id = wfpm_pre.wf_id AND st_base1.wf_ser_no = wfpm_pre.wf_ser_no"
            + " INNER JOIN fw_wf_progress_management AS wfpm_pos ON st_base2.wf_id = wfpm_pos.wf_id AND st_base2.wf_ser_no = wfpm_pos.wf_ser_no"
            + " INNER JOIN fw_status_master AS sm1 ON wfpm_pre.status_code = sm1.status"
            + " INNER JOIN fw_status_master AS sm2 ON wfpm_pos.status_code = sm2.status")) {
      int idx = 1;
      ps.setString(idx++, wfId);
      ps.setString(idx++, wfId);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          action = new FWWFAction();
          action.setPreStatus(rs.getString("pre_status_code"));
          action.setPreStatusName(rs.getString("pre_status_name"));
          action.setPostStatus(rs.getString("post_status_code"));
          action.setPostStatusName(rs.getString("post_status_name"));
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
        "SELECT wfrt.action_code AS action_code, wfrt.action AS action, wfrt.pre_status AS pre_status, sm1.status_name AS pre_status_name, wfrt.post_status AS post_status, sm2.status_name AS status_name, CASE WHEN wfrt2.pre_status IS NULL THEN 'true' ELSE 'false' END AS last_action_bool FROM fw_wf_rote AS wfrt"
            + " INNER JOIN fw_role_action AS ra ON wfrt.action_code = ra.action_code"
            + " LEFT JOIN fw_status_master AS sm1 ON wfrt.pre_status = sm1.status"
            + " LEFT JOIN fw_status_master AS sm2 ON wfrt.post_status = sm2.status"
            + " LEFT JOIN (SELECT pre_status FROM fw_wf_rote GROUP BY pre_status) AS wfrt2 ON wfrt.post_status = wfrt2.pre_status"
            + " WHERE wfrt.pre_status = ? AND ra.role = ?"
            + " ORDER BY wfrt.action_code ASC")) {
      int idx = 1;
      ps.setString(idx++, status);
      ps.setString(idx++, role);
      try (FWResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          FWWFAction action = new FWWFAction();
          action.setActionCode(rs.getString("action_code"));
          action.setAction(rs.getString("action"));
          action.setPreStatus(rs.getString("pre_status"));
          action.setPreStatusName(rs.getString("pre_status_name"));
          action.setPostStatus(rs.getString("post_status"));
          action.setPostStatusName(rs.getString("status_name"));
          action.setLastAction(Boolean.valueOf(rs.getString("last_action_bool")));
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
        "SELECT wfrt.action_code AS action_code, wfrt.action AS action, wfrt.pre_status AS pre_status, sm1.status_name AS pre_status_name, wfrt.post_status AS post_status, sm2.status_name AS status_name, CASE WHEN wfrt2.pre_status IS NULL THEN 'true' ELSE 'false' END AS last_action_bool FROM fw_wf_rote AS wfrt"
            + " INNER JOIN fw_role_action AS ra ON wfrt.action_code = ra.action_code"
            + " LEFT JOIN fw_status_master AS sm1 ON wfrt.pre_status = sm1.status"
            + " LEFT JOIN fw_status_master AS sm2 ON wfrt.post_status = sm2.status"
            + " LEFT JOIN (SELECT pre_status FROM fw_wf_rote GROUP BY pre_status) AS wfrt2 ON wfrt.post_status = wfrt2.pre_status"
            + " WHERE ra.role = ? AND wfrt.action_code = ?")) {
      int idx = 1;
      ps.setString(idx++, role);
      ps.setString(idx++, actionCode);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          action = new FWWFAction();
          action.setActionCode(rs.getString("action_code"));
          action.setAction(rs.getString("action"));
          action.setPreStatus(rs.getString("pre_status"));
          action.setPreStatusName(rs.getString("pre_status_name"));
          action.setPostStatus(rs.getString("post_status"));
          action.setPostStatusName(rs.getString("status_name"));
          action.setLastAction(Boolean.valueOf(rs.getString("last_action_bool")));
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
        "SELECT wfrt.action_code AS action_code, wfrt.action AS action, wfrt.pre_status AS pre_status, sm1.status_name AS pre_status_name, wfrt.post_status AS post_status, sm2.status_name AS status_name, CASE WHEN wfrt2.pre_status IS NULL THEN 'true' ELSE 'false' END AS last_action_bool FROM fw_wf_rote AS wfrt"
            + " INNER JOIN fw_role_action AS ra ON wfrt.action_code = ra.action_code"
            + " LEFT JOIN fw_status_master AS sm1 ON wfrt.pre_status = sm1.status"
            + " LEFT JOIN fw_status_master AS sm2 ON wfrt.post_status = sm2.status"
            + " LEFT JOIN (SELECT pre_status FROM fw_wf_rote GROUP BY pre_status) AS wfrt2 ON wfrt.post_status = wfrt2.pre_status"
            + " WHERE wfrt.pre_status = ? AND ra.role = ? AND wfrt.action_code = ?")) {
      int idx = 1;
      ps.setString(idx++, status);
      ps.setString(idx++, role);
      ps.setString(idx++, actionCode);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          action = new FWWFAction();
          action.setActionCode(rs.getString("action_code"));
          action.setAction(rs.getString("action"));
          action.setPreStatus(rs.getString("pre_status"));
          action.setPreStatusName(rs.getString("pre_status_name"));
          action.setPostStatus(rs.getString("post_status"));
          action.setPostStatusName(rs.getString("status_name"));
          action.setLastAction(Boolean.valueOf(rs.getString("last_action_bool")));
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
        "SELECT MAX(wf_ser_no) AS max_wf_ser_no FROM fw_wf_log"
            + " WHERE wf_id = ?")) {
      ps.setString(1, wfLog.getWfId());
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          wfSerNo = rs.getInt("max_wf_ser_no") + 1;
        }
      }
    }
    // 履歴登録処理
    try (FWPreparedStatement ps = con.prepareStatement(
        "insert into fw_wf_log (wf_id, wf_ser_no, action_date, action_owner, action_code, status_code, description) values(?, ?, ?, ?, ?, ?, ?)")) {
      int idx = 1;
      ps.setString(idx++, wfLog.getWfId());
      ps.setInt(idx++, wfSerNo);
      ps.setTimestamp(idx++, wfLog.getActionDate());
      ps.setString(idx++, wfLog.getActionOwner());
      ps.setString(idx++, wfLog.getActionCode());
      ps.setString(idx++, wfLog.getStatusCode());
      ps.setString(idx++, wfLog.getDescription());
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
        "SELECT wflg.wf_id AS wf_id, wflg.wf_ser_no AS wf_ser_no, wflg.action_date AS action_date, wflg.action_owner AS action_owner, ur.name AS action_owner_name, wflg.action_code AS action_code, wfrt.action AS action, wflg.status_code AS status_code, sm.status_name AS status_name, wflg.description AS description FROM fw_wf_log AS wflg"
            + " LEFT JOIN fw_user AS ur ON wflg.action_owner = ur.id"
            + " LEFT JOIN fw_wf_rote AS wfrt ON wflg.action_code = wfrt.action_code"
            + " LEFT JOIN fw_status_master AS sm ON wflg.status_code = sm.status"
            + " WHERE wflg.wf_id = ? AND wflg.wf_ser_no = ?")) {
      int idx = 1;
      ps.setString(idx++, wfId);
      ps.setInt(idx++, wfSerNo);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          wfLog = new FWWFLog();
          wfLog.setWfId(rs.getString("wf_id"));
          wfLog.setWfSerNo(rs.getInt("wf_ser_no"));
          wfLog.setActionDate(rs.getTimestamp("action_date"));
          wfLog.setActionOwner(rs.getString("action_owner"));
          wfLog.setActionOwnerName(rs.getString("action_owner_name"));
          wfLog.setActionCode(rs.getString("action_code"));
          wfLog.setActionName(rs.getString("action"));
          wfLog.setStatusCode(rs.getString("status_code"));
          wfLog.setStatusName(rs.getString("status_name"));
          wfLog.setDescription(rs.getString("description"));
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
        "SELECT wflg.wf_id AS wf_id, wflg.wf_ser_no AS wf_ser_no, wflg.action_date AS action_date, wflg.action_owner AS action_owner, ur.name AS action_owner_name, wflg.action_code AS action_code, wfrt.action AS action, wflg.status_code AS status_code, sm.status_name AS status_name, wflg.description AS description FROM fw_wf_log AS wflg"
            + " LEFT JOIN fw_user AS ur ON wflg.action_owner = ur.id"
            + " LEFT JOIN fw_wf_rote AS wfrt ON wflg.action_code = wfrt.action_code"
            + " LEFT JOIN fw_status_master AS sm ON wflg.status_code = sm.status"
            + " WHERE wflg.wf_id = ?"
            + " ORDER BY wflg.wf_ser_no ASC")) {
      ps.setString(1, wfId);
      try (FWResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          FWWFLog wfLog = new FWWFLog();
          wfLog.setWfId(rs.getString("wf_id"));
          wfLog.setWfSerNo(rs.getInt("wf_ser_no"));
          wfLog.setActionDate(rs.getTimestamp("action_date"));
          wfLog.setActionOwner(rs.getString("action_owner"));
          wfLog.setActionOwnerName(rs.getString("action_owner_name"));
          wfLog.setActionCode(rs.getString("action_code"));
          wfLog.setActionName(rs.getString("action"));
          wfLog.setStatusCode(rs.getString("status_code"));
          wfLog.setStatusName(rs.getString("status_name"));
          wfLog.setDescription(rs.getString("description"));
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
      int idx = 1;
      ps.setString(idx++, wfProgressManagement.getWfId());
      ps.setInt(idx++, wfProgressManagement.getWfSerNo());
      ps.setString(idx++, wfProgressManagement.getActionCode());
      ps.setString(idx++, wfProgressManagement.getStatusCode());
      ps.setTimestamp(idx++, wfProgressManagement.getCreateDate());
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
      int idx = 1;
      ps.setString(idx++, wfId);
      ps.setString(idx++, wfId);
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
