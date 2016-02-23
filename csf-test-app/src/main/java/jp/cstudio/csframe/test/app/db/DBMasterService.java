package jp.cstudio.csframe.test.app.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.csframe.db.FWConnectionManager;
import com.csframe.db.FWPreparedStatement;
import com.csframe.db.FWResultSet;
import com.csframe.db.FWStatement;
import com.csframe.db.FWTransactional;
import com.csframe.db.FWTransactional.FWTxType;
import com.csframe.log.FWLogger;

import jp.cstudio.csframe.test.app.db.dto.Test;

@ApplicationScoped
public class DBMasterService {

  @Inject
  private FWConnectionManager cm;

  @Inject
  private FWLogger logger;

  @FWTransactional(dataSourceName = "jdbc/ds_csf", value = FWTxType.REQUIRED)
  public List<Test> selectAll() throws SQLException {

    logger.info("DBマスタ参照");
    try (FWStatement stmt = cm.getConnection().createStatement();
        FWResultSet rs = stmt.executeQuery("select * from test");) {
      List<Test> results = new ArrayList<>();
      while (rs.next()) {
        Test t = new Test();
        t.setKey(rs.getString("key"));
        t.setValue(rs.getString("value"));
        results.add(t);
      }
      return results;
    }
  }

  @FWTransactional(dataSourceName = "jdbc/ds_csf", value = FWTxType.REQUIRED)
  public void update(Test test) throws SQLException {

    logger.info("テストテーブル更新");
    logger.debug("DATA=" + test);
    try (FWPreparedStatement stmt =
        cm.getConnection().prepareStatement("update test set value = ? where key = ?")) {
      stmt.setString(1, test.getValue());
      stmt.setString(2, test.getKey());
      stmt.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/ds_csf")
  public void insert(Test test) throws SQLException {

    logger.info("テストテーブル追加");
    logger.debug("DATA=" + test);
    try (FWPreparedStatement stmt =
        cm.getConnection().prepareStatement("insert into test values(?, ?)")) {
      stmt.setString(1, test.getKey());
      stmt.setString(2, test.getValue());
      stmt.executeUpdate();
    }
  }

  @FWTransactional(dataSourceName = "jdbc/ds_csf")
  public void delete(String key) throws SQLException {

    logger.info("テストテーブル削除");
    logger.debug("DATA=" + key);
    try (FWPreparedStatement stmt =
        cm.getConnection().prepareStatement("delete from test where key = ?")) {
      stmt.setString(1, key);
      stmt.executeUpdate();
    }
  }

}
