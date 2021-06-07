package com.handywedge.user;

import java.sql.SQLException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWTransactional;

@ApplicationScoped
public class FWInnerUserService {

  @Inject
  private FWConnectionManager cm;

  @FWTransactional(dataSourceName = "jdbc/fw")
  public FWUser getUserByToken(String token) throws SQLException {

    // ロケールは今の所未実装
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT fu.id, fu.name, frm.role, frm.role_name FROM fw_api_token fat");
    sql.append(" INNER JOIN fw_user fu ON fu.id = fat.id");
    sql.append(" LEFT JOIN fw_role_master frm ON frm.role = fu.role");
    sql.append(" WHERE fat.token = ?");
    try (FWPreparedStatement ps = cm.getConnection().prepareStatement(sql.toString())) {
      ps.setString(1, token);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          FWFullUser user = new FWUserImpl();
          user.setId(rs.getString("id"));
          user.setName(rs.getString("name"));
          user.setRole(rs.getString("role"));
          user.setRoleName(rs.getString("role_name"));
          return user;
        } else {
          return null;
        }
      }
    }
  }
}
