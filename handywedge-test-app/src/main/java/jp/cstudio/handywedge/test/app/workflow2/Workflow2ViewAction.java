package jp.cstudio.handywedge.test.app.workflow2;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.handywedge.common.FWStringUtil;
import com.handywedge.workflow.FWWFAction;
import com.handywedge.workflow.FWWFException;
import com.handywedge.workflow.FWWFLog;
import com.handywedge.workflow.FWWFManager;

import lombok.Getter;
import lombok.Setter;

/*
 * ワークフローデータの一覧表示、新規登録を行うためのActionクラス。
 */
@ViewScoped
@Named("workflow2View")
public class Workflow2ViewAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private transient FWWFManager wfManager;

  @Inject
  private Workflow2Service daoService; // ワークフローテーブル管理（業務データ）

  @Getter
  private List<Workflow2> wfList; // 一覧表示用リスト

  @Setter
  @Getter
  private String subject; // [タイトル]データ追加用

  @Setter
  @Getter
  private String body; // [内容]データ追加用

  @Getter
  private List<FWWFAction> wfActions; // 実行可能なアクションリスト※[保存]アクション用
  @Getter
  private boolean actionFlag; // ボタン表示・非表示用

  public void init() {
    try {
      view();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void view() throws SQLException {
    // 初期処理としてデータ一覧を表示
    wfList = daoService.selectAll();
    for (int i = 0; i < wfList.size(); i++) {
      // ■WF処理：文書ステータスの取得
      // 文書のステータス情報をWFエンジンより取得
      // WFエンジンでは問合せ回数分、DBへのアクセスが発生するため、表示用に業務データにステータスを保持していても良い。
      // 特にテキスト出力機能などの対象レコード数が多い場合はレスポンス悪化が想定されるため検討すること。
      Workflow2 wf = wfList.get(i);
      wf.setStatus(wfManager.getStatus(wf.getWfId()));
      wfList.set(i, wf);
    }

    // ■WF処理：実行可能なアクション（初期）の取得
    // WF初期アクションではWFIDを保有していない為、アクションコードを指定し実施可能なアクションを取得する
    // 当該はサンプルとしての実装例。初期アクションとなるデータの登録画面は、通常URL-ロールにて制御を行うことを想定する。その場合、ボタンの表示・非表示制御は不要と思慮。
    // 通常は１アクションを指定するが、当該は複合フローの検証用として定義している為、2アクションのボタン表示を制御する
    wfActions = new ArrayList<>();
    //A部申請用「保存[A部]」アクション
    FWWFAction action = wfManager.getAction("ACT10000");
    if (action != null)
      wfActions.add(action);
    //B部申請用「保存[B部]」アクション
    action = wfManager.getAction("ACT20000");
    if (action != null)
      wfActions.add(action);

    // [保存]ボタンの表示・非表示
    if (wfActions.size() != 0) {
      actionFlag = true;
    }
  }

  public void addData(String actionCode) throws FWWFException {
    // 業務データチェック（簡易実装）
    if (FWStringUtil.isEmpty(subject) || FWStringUtil.isEmpty(body)) {
      return;
    }
    // ■WF処理：ワークフロー処理（アクション）の実行
    // 初期アクション実行）
    // WFアクションの実行メソッドである#doInitAction()の引数には、アクションコードを指定する
    // #doInitAction()の返り値はWFエンジンにて登録した履歴オブジェクト（FWWFLog）を返却。当該オブジェクト内に採番したWFIDがあり、業務側で保有すること。
    //      FWWFLog wfLog = wfManager.doInitAction(action);
    FWWFLog wfLog = wfManager.doInitAction(actionCode);

    // 業務データ登録
    Workflow2 workflow = new Workflow2();
    workflow.setSubject(subject);
    workflow.setBody(body);
    workflow.setWfId(wfLog.getWfId()); // 業務データとしてWFIDを保有すること
    try {
      daoService.insert(workflow);
      view();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String viewDetailAction(String selectId) {
    // 詳細画面へ遷移
    return String.format("/contents/workflow2/workflow2Detail.xhtml?faces-redirect=true&selectId=%s", selectId);
  }
}
