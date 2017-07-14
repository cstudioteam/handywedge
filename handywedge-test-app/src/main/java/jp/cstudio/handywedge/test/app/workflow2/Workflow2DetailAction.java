package jp.cstudio.handywedge.test.app.workflow2;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.handywedge.user.FWUser;
import com.handywedge.workflow.FWWFAction;
import com.handywedge.workflow.FWWFException;
import com.handywedge.workflow.FWWFLog;
import com.handywedge.workflow.FWWFManager;

import lombok.Getter;
import lombok.Setter;

/*
 * ワークフローデータを承認/否認を行うためのActionクラス。
 */
@ViewScoped
@Named("workflow2Detail")
public class Workflow2DetailAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private transient FWWFManager wfManager;
  @Inject
  private transient Workflow2Service daoService; // ワークフローテーブル管理（業務データ）
  @Getter
  @Inject
  private transient FWUser user;

  @Getter
  private Workflow2 workflow; // 業務データ
  @Getter
  private List<FWWFAction> wfActions; // 実行可能なアクションリスト
  @Getter
  private List<FWWFLog> wfLogs; // 対象データの履歴リスト
  @Setter
  @Getter
  private String selectId; // 選択された文書ID
  @Getter
  private boolean doActionFlg; // 承認/否認のコメント欄の表示・非表示フラグ
  @Setter
  @Getter
  private String description; // 承認/否認の回付コメント

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
    // ■WF処理：文書ステータスの取得
    // 文書のステータス情報をWFエンジンより取得
    workflow.setStatus(wfManager.getStatus(workflow.getWfId()));
    // ■WF処理：対象文書の履歴リストの取得
    // 業務データに保有しているWFIDを引数に履歴リストを取得する
    wfLogs = wfManager.getWFLogs(workflow.getWfId());
    // ■WF処理：実行可能なアクションの取得
    // 業務データに保有しているWFIDを引数に実行可能なアクションリスト（承認系＋否認系）を取得する
    wfActions = wfManager.getActions(workflow.getWfId());
    // 回付コメント入力欄の表示・非表示
    doActionFlg = wfActions.size() != 0;
  }

  public String doAction(String actionCode) throws SQLException, FWWFException {
    // ■WF処理：指定アクションの実行判定
    // 業務データに保有しているWFID、及びWFボタン押下により取得したアクションIDを引数に実行判定を行う
    // 実行不可の場合、FWWFExceptionがthrowされる
    FWWFAction wfAction = wfManager.checkAction(workflow.getWfId(), actionCode);
    // ■WF処理：指定アクションの実行
    // WFアクションを実行し（WFエンジン内に保有しているステータスを進める）、またWF履歴を書き込みます
    FWWFLog wfLog = wfManager.doAction(wfAction, description);

    // 業務処理
    // サンプルでは更新日付の更新を行います（WF履歴と同時刻をセット）
    workflow.setUpdateDate(wfLog.getActionDate());
    daoService.update(workflow);

    return "/contents/workflow2/workflow2View.xhtml?faces-redirect=true"; // アクション実行後は一覧画面へ遷移
  }
}
