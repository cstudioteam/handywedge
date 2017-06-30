/*
 * Copyright (c) 2016 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.notice;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.handywedge.common.FWConstantCode;
import com.handywedge.common.FWRuntimeException;
import com.handywedge.db.FWConnection;
import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWStatement;
import com.handywedge.db.FWTransactional;
import com.handywedge.log.FWLogger;
import com.handywedge.notice.FWNotice;
import com.handywedge.notice.FWNoticeManager;


@RequestScoped
public class FWNoticeManagerImpl implements FWNoticeManager {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWLogger logger;

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public void register(FWNotice notice) {
    long startTime = logger.perfStart("register");
    logger.debug("register notice={}", notice);
    String sql = "INSERT INTO fw_notice (id, notice) VALUES (?, ?)";
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      int i = 0;
      ps.setInt(++i, notice.getId());
      ps.setString(++i, notice.getNotice());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    logger.perfEnd("register", startTime);
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public FWNotice get(int id) {
    long startTime = logger.perfStart("get");
    logger.debug("get id={}", id);
    String sql = "SELECT * FROM fw_notice WHERE id = ?";
    FWNotice result = null;
    FWConnection con = cm.getConnection();
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      int i = 0;
      ps.setInt(++i, id);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          result = new FWNotice();
          result.setId(rs.getInt("id"));
          result.setNotice(rs.getString("notice"));
          result.setCreateDate(rs.getTimestamp("create_date"));
          result.setUpdateDate(rs.getTimestamp("update_date"));
        }
      }
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    logger.debug("get return notice={}", result);
    logger.perfEnd("get", startTime);
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public List<FWNotice> list() {
    long startTime = logger.perfStart("list");
    String sql = "SELECT * FROM fw_notice ORDER BY update_date DESC";
    List<FWNotice> results = new ArrayList<>();
    FWConnection con = cm.getConnection();
    try (FWStatement st = con.createStatement()) {
      try (FWResultSet rs = st.executeQuery(sql)) {
        while (rs.next()) {
          FWNotice result = new FWNotice();
          result.setId(rs.getInt("id"));
          result.setNotice(rs.getString("notice"));
          result.setCreateDate(rs.getTimestamp("create_date"));
          result.setUpdateDate(rs.getTimestamp("update_date"));
          results.add(result);
        }
      }
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    logger.debug("list return list_size={}", results.size());
    logger.perfEnd("list", startTime);
    return results;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public int update(FWNotice notice) {
    long startTime = logger.perfStart("update");
    logger.debug("update notice={}", notice);
    String sql = "UPDATE fw_notice SET notice = ?, update_date = ? WHERE id = ?";
    FWConnection con = cm.getConnection();
    int result = 0;
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      int i = 0;
      ps.setString(++i, notice.getNotice());
      ps.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
      ps.setInt(++i, notice.getId());
      result = ps.executeUpdate();
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    logger.debug("update return={}", result);
    logger.perfEnd("update", startTime);
    return result;
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  @Override
  public int delete(int id) {
    long startTime = logger.perfStart("delete");
    logger.debug("delete id={}", id);
    String sql = "DELETE FROM fw_notice WHERE id = ?";
    FWConnection con = cm.getConnection();
    int result = 0;
    try (FWPreparedStatement ps = con.prepareStatement(sql)) {
      int i = 0;
      ps.setInt(++i, id);
      result = ps.executeUpdate();
    } catch (SQLException e) {
      throw new FWRuntimeException(FWConstantCode.DB_FATAL, e);
    }
    logger.debug("delete return={}", result);
    logger.perfEnd("delete", startTime);
    return result;
  }
}
