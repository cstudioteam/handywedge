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

import javax.inject.Inject;

import com.csframe.db.FWConnection;
import com.csframe.db.FWConnectionManager;
import com.csframe.db.FWPreparedStatement;
import com.csframe.db.FWResultSet;
import com.csframe.db.FWTransactional;

public class FWRoleService {

  @Inject
  private FWConnectionManager cm;

  @FWTransactional(dataSourceName = "jdbc/fw")
  public List<String> getActionCode(String role) throws SQLException {

    FWConnection con = cm.getConnection();

    // テーブル名は仮
    FWPreparedStatement ps = null;
    FWResultSet rs = null;
    List<String> result = new ArrayList<>();
    try {
      ps = con.prepareStatement("select * from fw_role_action where role = ?");
      ps.setString(1, role);
      rs = ps.executeQuery();
      while (rs.next()) {
        result.add(rs.getString("role"));
      }
      return result;
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
        }
      }
      if (ps != null) {
        try {
          ps.close();
        } catch (SQLException e) {
        }
      }
    }
  }
}
