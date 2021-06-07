package jp.cstudio.handywedge.test.app.db;

import java.sql.SQLException;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.primefaces.event.CellEditEvent;

import com.handywedge.common.FWStringUtil;
import com.handywedge.log.FWLogger;

import jp.cstudio.handywedge.test.app.db.dto.Test;
import lombok.Getter;
import lombok.Setter;

@RequestScoped
@Named("dbMaster")
public class DBMasterAction {

  @Inject
  private DBMasterService service;

  @Getter
  private List<Test> test;

  @Setter
  @Getter
  private String addKey;

  @Setter
  @Getter
  private String addValue;

  @Inject
  private FWLogger logger;

  @PostConstruct
  public void init() {

    try {
      test = service.selectAll();
    } catch (SQLException e) {
      logger.error("DBマスタ参照でエラーが発生しました。", e);
    }
  }

  public void onCellEdit(CellEditEvent event) {

    String oldValue = (String) event.getOldValue();
    String newValue = (String) event.getNewValue();
    int idx = event.getRowIndex();

    // これが取得したいが取れない
    // System.out.println(event.getRowKey());

    if (newValue != null && !newValue.equals(oldValue)) {
      Test target = test.get(idx);
      target.setValue(newValue);
      try {
        service.update(target);
      } catch (SQLException e) {
        logger.error("DBマスタ更新でエラーが発生しました。", e);
      }
    }
  }

  public void addData() {

    if (FWStringUtil.isEmpty(addKey) || FWStringUtil.isEmpty(addValue)) {
      return;
    }
    Test t = new Test();
    t.setKey(addKey);
    t.setValue(addValue);
    try {
      service.insert(t);
      test = service.selectAll();
    } catch (SQLException e) {
      logger.error("DBマスタ登録でエラーが発生しました。", e);
    }
  }

  public void deleteData(String key) {

    if (!FWStringUtil.isEmpty(key)) {
      try {
        service.delete(key);
        test = service.selectAll();
      } catch (SQLException e) {
        logger.error("DBマスタ削除でエラーが発生しました。", e);
      }
    }
  }

}
