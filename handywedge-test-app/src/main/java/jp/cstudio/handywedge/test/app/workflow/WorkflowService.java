package jp.cstudio.handywedge.test.app.workflow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWStatement;
import com.handywedge.db.FWTransactional;
import com.handywedge.db.FWTransactional.FWTxType;
import com.handywedge.role.FWAction;
import com.handywedge.user.FWUser;


@ApplicationScoped
public class WorkflowService {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWUser user;

  @FWTransactional(dataSourceName = "jdbc/ds_handywedge", value = FWTxType.REQUIRED)
  public List<Workflow> selectAll() throws SQLException {

    try (FWStatement stmt = cm.getConnection().createStatement();
        FWResultSet rs = stmt.executeQuery("SELECT * FROM workflow ORDER BY id ASC")) {
      List<Workflow> results = new ArrayList<>();
      while (rs.next()) {
        Workflow wf = new Workflow();
        wf.setId(rs.getInt("id"));
        wf.setSubject(rs.getString("subject"));
        wf.setStatus(Workflow.Status.toStatus(rs.getString("status")));
        wf.setCreateDate(rs.getTimestamp("create_date"));
        wf.setUpdateDate(rs.getTimestamp("update_date"));
        results.add(wf);
      }
      return results;
    }
  }

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
          wf.setSubject(rs.getString("subject"));
          wf.setStatus(Workflow.Status.toStatus(rs.getString("status")));
          wf.setCreateDate(rs.getTimestamp("create_date"));
          wf.setUpdateDate(rs.getTimestamp("update_date"));
        }
      }
    }
    return wf;
  }

  @FWTransactional(dataSourceName = "jdbc/ds_handywedge")
  public void insert(Workflow workflow) throws SQLException {
    try (FWPreparedStatement stmt =
        cm.getConnection().prepareStatement("insert into workflow (subject, status) values(?, ?)")) {
      stmt.setString(1, workflow.getSubject());
      stmt.setString(2, workflow.getStatus().toString());
      stmt.executeUpdate();
    }
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
    return upCnt;
  }
}
