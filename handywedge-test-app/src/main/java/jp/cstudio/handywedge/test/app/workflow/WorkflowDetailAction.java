package jp.cstudio.handywedge.test.app.workflow;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.handywedge.log.FWLogger;
import com.handywedge.role.FWAction;
import com.handywedge.role.FWRoleException;
import com.handywedge.role.FWRoleManager;
import com.handywedge.user.FWUser;

import lombok.Getter;
import lombok.Setter;

/*
 * ワークフローデータを承認/否認を行うためのActionクラス。
 */
@ViewScoped
@Named("workflowDetail")
public class WorkflowDetailAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private transient FWRoleManager roleManager; // FWのロールサービス

  @Inject
  private transient FWLogger logger; // FWのロガー

  @Inject
  private transient WorkflowService daoService; // ワークフローテーブル管理

  @Getter
  @Inject
  private transient FWUser user;

  @Getter
  private Workflow workflow; // 閲覧中のワークフローデータ

  @Getter
  private FWAction agreeAction; // 閲覧中のデータに対する承認アクション
  @Getter
  private FWAction disagreeAction; // 閲覧中のデータに対する否認アクション
  @Getter
  private boolean agreeActionFlag;
  @Getter
  private boolean disagreeActionFlag;
  @Setter
  @Getter
  private String selectId;

  public void init() {
    // 文書IDよりデータ（詳細）表示
    try {
      if (selectId != null) {
        view(selectId);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void view(String selectId) throws SQLException {
    workflow = daoService.select(Integer.parseInt(selectId));
    if (workflow == null) {
      throw new RuntimeException();
    }

    // 現在のワークフローステータスに対して承認権限のチェックを行い、権限がない場合はFWRoleExceptionとなる
    List<String> actions = roleManager.getActions(workflow.getStatus().toString(), user.getRole());

    // アクションコードの体系を以下とし実装。また表示ボタンは2つ（承認、否認）までの想定
    // ****1:承認系のアクション　****2:否認系のアクション
    if(actions != null) {
      for (int i =0; i < actions.size(); i++) {
        try {
          FWAction action = roleManager.checkAction(workflow.getStatus().toString(), actions.get(i), user.getRole());
          if (action.getActionCode().endsWith("1")) {
            // 承認系アクション
            agreeAction = action;
            agreeActionFlag = true; //ボタン表示をON
          } else if (action.getActionCode().endsWith("2")) {
            // 否認系アクション
            disagreeAction = action;
            disagreeActionFlag = true; //ボタン表示をON
          } else {
            // その他（****0 の「保存」のみ）→単独なので承認系に紐付ける
            agreeAction = action;
            agreeActionFlag = true; //ボタン表示をON
          }
        } catch (FWRoleException e) {
          // 権限がない場合、もみ消し
        }
      }
    }
  }

  public String agree() throws SQLException {
    // 承認アクション ※本来は権限チェックを行う
    logger.info("ワークフローID[{}]が承認されました。", workflow.getId());
    daoService.doAgree(workflow, agreeAction);
    return "/contents/workflow/workflowView.xhtml"; // アクション実行後は一覧画面へ遷移
  }

  public String disagree() throws SQLException {
    // 否認アクション ※本来は権限チェックを行う
    logger.info("ワークフローID[{}]が否認されました。", workflow.getId());
    daoService.doAgree(workflow, disagreeAction);
    return "/contents/workflow/workflowView.xhtml"; // アクション実行後は一覧画面へ遷移
  }
}
