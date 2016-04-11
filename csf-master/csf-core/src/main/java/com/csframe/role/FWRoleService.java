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

import com.csframe.db.FWConnection;
import com.csframe.db.FWConnectionManager;
import com.csframe.db.FWPreparedStatement;
import com.csframe.db.FWResultSet;
import com.csframe.db.FWTransactional;

@ApplicationScoped
public class FWRoleService {

  @Inject
  private FWConnectionManager cm;

  @FWTransactional(dataSourceName = "jdbc/fw")
  public List<String> getActions(String currentStatus, String role) throws SQLException {

    FWConnection con = cm.getConnection();
    List<String> result = new ArrayList<>();
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT post_status FROM fw_action AS a"
            + " INNER JOIN fw_role_action AS r_a ON a.action_code = r_a.action_code"
            + " where pre_status = ? AND role = ?")) {
      ps.setString(1, currentStatus);
      ps.setString(2, role);
      try (FWResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          result.add(rs.getString(1));
        }
        return result;
      }
    }
  }

  @FWTransactional(dataSourceName = "jdbc/fw")
  public String getActionCode(String preStatus, String postStatus, String role)
      throws SQLException {

    FWConnection con = cm.getConnection();
    String result = null;
    try (FWPreparedStatement ps = con.prepareStatement(
        "SELECT a.action_code FROM fw_action AS a"
            + " INNER JOIN fw_role_action AS r_a ON a.action_code = r_a.action_code"
            + " WHERE pre_status = ? AND post_status = ? AND role = ?")) {

      ps.setString(1, preStatus);
      ps.setString(2, postStatus);
      ps.setString(3, role);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          result = rs.getString(1);
        }
        return result;
      }
    }
  }
}
