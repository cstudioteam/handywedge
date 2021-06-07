package jp.cstudio.handywedge.test.app.db;

import java.sql.SQLException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWTransactional;
import com.handywedge.db.FWTransactional.FWTxType;
import com.handywedge.role.FWAction;
import com.handywedge.user.FWUser;

import jp.cstudio.handywedge.test.app.db.dto.Workflow;
import jp.cstudio.handywedge.test.app.db.dto.Workflow.Status;

/*
 * 実装サンプルコードで稼働はしません。
 */
@ApplicationScoped
public class WorkflowService {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWUser user;

  @FWTransactional(dataSourceName = "jdbc/ds_handywedge", value = FWTxType.REQUIRED)
  public Workflow select(int id) throws SQLException {

    Workflow wf = null;
    try (FWPreparedStatement ps =
        cm.getConnection().prepareStatement("SELECT * FROM workflow WHERE id = ?")) {
      ps.setInt(1, id);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          wf = new Workflow();
          wf.setId(rs.getInt("id"));
          wf.setStatus(Status.toStatus(rs.getString("status")));
          // 以下省略
        }
      }
    }
    return wf;
  }

  @FWTransactional(dataSourceName = "jdbc/ds_handywedge", value = FWTxType.REQUIRED)
  public int doAgree(Workflow workflow, FWAction action) throws SQLException {

    StringBuilder sql = new StringBuilder();
    sql.append("UPDATE workflow SET status = ?");
    sql.append(", update_date = CURRENT_TIMESTAMP");
    sql.append(" WHERE id = ?");
    int upCnt;
    try (FWPreparedStatement ps = cm.getConnection().prepareStatement(sql.toString())) {
      int idx = 1;
      ps.setString(idx++, action.getPostStatus());
      ps.setInt(idx++, workflow.getId());
      upCnt = ps.executeUpdate();
    }
    if (upCnt == 1) {
      sql = new StringBuilder();
      sql.append("INSERT INTO action_history (action_code, user_id, description) VALUES(?, ?, ?)");
      try (FWPreparedStatement ps = cm.getConnection().prepareStatement(sql.toString())) {
        int idx = 1;
        ps.setString(idx++, action.getActionCode());
        ps.setString(idx++, user.getId());
        ps.setString(idx++, "description");
        ps.executeUpdate();
      }
    }
    return upCnt;
  }
}
