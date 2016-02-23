package jp.cstudio.csframe.test.app.db;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.CellEditEvent;

import com.csframe.util.FWStringUtil;

import jp.cstudio.csframe.test.app.db.dto.Test;
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

  @PostConstruct
  public void init() {

    try {
      test = service.selectAll();
    } catch (SQLException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
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
        e.printStackTrace();
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
      e.printStackTrace();
    }
  }

  public void deleteData(String key) {

    if (!FWStringUtil.isEmpty(key)) {
      try {
        service.delete(key);
        test = service.selectAll();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

}
