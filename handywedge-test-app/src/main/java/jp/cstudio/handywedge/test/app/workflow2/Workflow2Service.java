package jp.cstudio.handywedge.test.app.workflow2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.handywedge.db.FWConnectionManager;
import com.handywedge.db.FWPreparedStatement;
import com.handywedge.db.FWResultSet;
import com.handywedge.db.FWStatement;
import com.handywedge.db.FWTransactional;
import com.handywedge.db.FWTransactional.FWTxType;

@ApplicationScoped
public class Workflow2Service {

  @Inject
  private FWConnectionManager cm;

  @FWTransactional(dataSourceName = "jdbc/ds_handywedge", value = FWTxType.REQUIRED)
  public List<Workflow2> selectAll() throws SQLException {

    try (FWStatement stmt = cm.getConnection().createStatement();
        FWResultSet rs = stmt.executeQuery("SELECT * FROM workflow2 ORDER BY id ASC")) {
      List<Workflow2> results = new ArrayList<>();
      while (rs.next()) {
        Workflow2 wf = new Workflow2();
        wf.setId(rs.getInt("id"));
        wf.setSubject(rs.getString("subject"));
        wf.setBody(rs.getString("body"));
        wf.setWfId(rs.getString("wf_id"));
        wf.setCreateDate(rs.getTimestamp("create_date"));
        wf.setUpdateDate(rs.getTimestamp("update_date"));
        results.add(wf);
      }
      return results;
    }
  }

  @FWTransactional(dataSourceName = "jdbc/ds_handywedge", value = FWTxType.REQUIRED)
  public Workflow2 select(int id) throws SQLException {
    Workflow2 wf = null;
    try (FWPreparedStatement ps = cm.getConnection().prepareStatement("SELECT * FROM workflow2 WHERE id = ?")) {
      ps.setInt(1, id);
      try (FWResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          wf = new Workflow2();
          wf.setId(rs.getInt("id"));
          wf.setSubject(rs.getString("subject"));
          wf.setBody(rs.getString("body"));
          wf.setWfId(rs.getString("wf_id"));
          wf.setCreateDate(rs.getTimestamp("create_date"));
          wf.setUpdateDate(rs.getTimestamp("update_date"));
        }
      }
    }
    return wf;
  }

  @FWTransactional(dataSourceName = "jdbc/ds_handywedge")
  public void insert(Workflow2 workflow) throws SQLException {
    try (FWPreparedStatement stmt = cm.getConnection()
        .prepareStatement("INSERT INTO workflow2 (subject, body, wf_id) VALUES(?, ?, ?)")) {
      stmt.setString(1, workflow.getSubject());
      stmt.setString(2, workflow.getBody());
      stmt.setString(3, workflow.getWfId());
      stmt.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/ds_handywedge", value = FWTxType.REQUIRED)
  public void update(Workflow2 workflow) throws SQLException {
    try (FWPreparedStatement ps = cm.getConnection().prepareStatement("UPDATE workflow2 SET update_date = ? WHERE id = ?")) {
      ps.setTimestamp(1, workflow.getUpdateDate());
      ps.setInt(2, workflow.getId());
      ps.executeUpdate();
    }
  }
}
