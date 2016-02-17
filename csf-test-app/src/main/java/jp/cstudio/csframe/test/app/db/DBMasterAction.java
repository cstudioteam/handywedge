package jp.cstudio.csframe.test.app.db;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.CellEditEvent;

import jp.cstudio.csframe.test.app.db.dto.Test;
import lombok.Getter;

@RequestScoped
@Named("dbMaster")
public class DBMasterAction {

  @Inject
  private DBMasterService service;

  @Getter
  private List<Test> test;

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

}
