package jp.cstudio.handywedge.test.app.workflow;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.handywedge.common.FWStringUtil;
import com.handywedge.role.FWAction;
import com.handywedge.role.FWRoleException;
import com.handywedge.role.FWRoleManager;

import lombok.Getter;
import lombok.Setter;

/*
 * ワークフローデータの一覧表示、新規登録を行うためのActionクラス。
 */
@RequestScoped
@Named("workflowView")
public class WorkflowViewAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private transient FWRoleManager roleManager; // FWのロールサービス

  @Inject
  private WorkflowService daoService; // ワークフローテーブル管理

  @Getter
  private List<Workflow> wfList; // 一覧表示用リスト

  @Setter
  @Getter
  private String addSubject; // [タイトル]データ追加用

  @Getter
  private FWAction action; // [保存]アクション専用
  @Getter
  private boolean actionFlag; // ボタン表示・非表示用

  @PostConstruct
  public void init() {
    try {
      // 初期処理としてデータ一覧を表示
      wfList = daoService.selectAll();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    try {
      // [保存]ボタンの表示・非表示
      // 新規作成用の[保存]ボタンなので現ステータスは”Nothing”固定。
      action = roleManager.checkAction("Nothing", Workflow.Status.STATUS001.getViewStatus());
      actionFlag = true;
    } catch (FWRoleException e) {
      // 権限がない場合、もみ消し
    }
  }

  public void addData() {
    // 新規データ作成 ※本来は権限チェックを行う
    if (FWStringUtil.isEmpty(addSubject)) {
      return;
    }
    Workflow workflow = new Workflow();
    workflow.setSubject(addSubject);
    workflow.setStatus(Workflow.Status.STATUS001);
    try {
      daoService.insert(workflow);
      wfList = daoService.selectAll();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String viewDetailAction(String selectId) {
    // 詳細画面へ遷移
    return String.format("/contents/workflow/workflowDetail.xhtml?faces-redirect=true&selectId=%s", selectId);
  }
}
