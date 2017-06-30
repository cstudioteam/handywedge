package jp.cstudio.handywedge.test.app.workflow;

import java.io.Serializable;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.handywedge.log.FWLogger;
import com.handywedge.role.FWAction;
import com.handywedge.role.FWRoleException;
import com.handywedge.role.FWRoleManager;

import jp.cstudio.handywedge.test.app.db.WorkflowService;
import jp.cstudio.handywedge.test.app.db.dto.Workflow;
import jp.cstudio.handywedge.test.app.db.dto.Workflow.Status;
import lombok.Getter;
import lombok.Setter;


/*
 * ワークフローデータを承認/否認を行うためのActionクラス。
 * 実装サンプルコードで稼働はしません。
 */
@ViewScoped
@Named
public class WorkflowAgreeAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private FWRoleManager roleManager; // FWのロールサービス

  @Inject
  private FWLogger logger; // FWのロガー

  @Inject
  private WorkflowService daoService; // ワークフローテーブル管理

  @Getter
  private Workflow workflow; // 閲覧中のワークフローデータ

  @Setter
  @Getter
  private int id; // 画面からのクエリーパラメータ

  private FWAction agreeAction; // 閲覧中のデータに対する承認アクション
  private FWAction disagreeAction; // 閲覧中のデータに対する否認アクション

  @PostConstruct
  // 例外の出ない初期処理
  public void init() {}

  // DBアクセスなど例外が発生しうる初期処理
  public void viewAction() throws SQLException, FWRoleException {
    workflow = daoService.select(id); // 選択されたデータをテーブルから取得
    if (workflow == null) {
      throw new RuntimeException();
    }

    Status postStatus; // 遷移しうるステータス
    switch (workflow.getStatus()) {
      case SHONIN1_MACHI:
        postStatus = Status.SHONIN2_MACHI;
        break;
      case SHONIN2_MACHI:
        postStatus = Status.SHONIN_SUMI;
        break;
      default:
        throw new RuntimeException();
    }

    // 現在のワークフローステータスに対して承認権限のチェックを行い、権限がない場合はFWRoleExceptionとなる
    agreeAction = roleManager.checkAction(workflow.getStatus().toString(), postStatus.toString());
    disagreeAction =
        roleManager.checkAction(workflow.getStatus().toString(), Status.HININ_SUMI.toString());
  }

  public String doAgree() throws SQLException {
    logger.info("ワークフローID[{}]が承認されました。", workflow.getId());
    daoService.doAgree(workflow, agreeAction); // ステータス更新

    return "workflow_deatail"; // 閲覧画面に遷移
  }

  public String doDisagree() throws SQLException {
    logger.info("ワークフローID[{}]が否認されました。", workflow.getId());
    daoService.doAgree(workflow, disagreeAction); // ステータス更新

    return "workflow_deatail"; // 閲覧画面に遷移
  }
}
