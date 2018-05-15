ワークフロー機能
====================
.. index::
    single: 前ステータス
    single: 後ステータス
    single: ワークフロールートテーブル
    single: ロール別アクションテーブル

**Handywedge** が提供するワークフロー機能について説明する。

--------------------
ワークフロー制御
--------------------
**Handywedge** は、次のワークフロー機能を提供する。

* ワークフローの開始

 | ワークフローアクションを実行し、ステータス遷移情報を **Handywedge** 内部で保有する。

 | また、当該情報を管理するワークフローIDを発行する。ワークフローIDは、以後のアクション実行、及びその他に提供する各機能のパラメータとなるため、業務データと共に保持する。

* アクションリストの取得

 | **Handywedge** は、ログイン中のユーザー情報ロール、及び :ref:`workflow-root` 、ロール別アクション情報より実行可能なアクションを返却。

 | **Handywedge** は、１つ前のステータスに戻るためのアクションを自動生成し否認アクションを返却する。

 | 合流するようなワークフローにおいても、合流前のステータスに戻すアクションの自動生成を可能とする為、 **Handywedge** では、ワークフローがどのようなルートでステータス遷移してきたかの情報を保持する。

* ワークフローアクションの実行

 | ワークフローアクションを実行しワークフローを進める。

* ワークフロー履歴の登録

 | **Handywedge** はワークフローアクション実行時に自動的にワークフローの履歴を登録する。

* ワークフロー履歴の取得

 | ワークフローIDを指定して、当該データのワークフロー履歴を取得する。


.. _workflow-root:

ワークフロールート
-----------------------
ワークフロールートとは、ワークフローのアクション情報を意味するもので、ワークフローアクション（例：部長承認）毎に当該アクションの前ステータス（例：部長承認待ち）と後ステータス（例：事業部長承認待ち）を定義した情報を指す。

.. actdiag::

    actdiag {

                   起票 -> 部長承認待ち;
                   部長承認待ち -> 部長承認 ;
                   部長承認 -> 事業部長承認待ち;
                   事業部長承認待ち -> 事業部長承認;

        lane 担当者 {
                            起票;
                            部長承認待ち  [shape = circle, textcolor = red];
        }

        lane 部長 {
                            部長承認 [color = gold];
                            事業部長承認待ち  [shape = circle, textcolor = red];
        }

        lane 事業部長 {
                            事業部長承認;
        }
    }



ワークフローテーブル設定
-------------------------------
ワークフロー機能を使用する場合は、 **Handywedge** が管理する次のテーブルを適切に設定する。

ワークフロールートテーブル（fw_wf_rote）
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
ワークフロールートを定義する。（※本定義には戻る（否認系）アクションの定義は行わない）

.. csv-table::
   :widths: 20 20 60
   :header: "項目名", "型", "詳細"

   "アクションコード", "string", "ワークフローアクションの一意なコード。"
   "アクション名", "string", "ワークフローアクションの名称。"
   "アクション前ステータス", "string", "当該アクションを実行する前のステータス（コード）。"
   "アクション後ステータス", "string", "当該アクションを実行した後のステータス（コード）。"


ロール別アクションテーブル（fw_role_action）
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
ロールで実行可能なアクションを定義する。

.. csv-table::
   :widths: 20 20 60
   :header: "項目名", "型", "詳細"

   "ロール", "string", "ユーザー情報に紐付くロール（コード）。"
   "アクションコード", "string", "当該ロールが実行可能なアクションコード。"


ステータスマスターテーブル（fw_status_master）
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
ワークフローのステータスを定義する。

.. csv-table::
   :widths: 20 20 60
   :header: "項目名", "型", "詳細"

   "スタータスコード", "string", "ステータスを表す一意なコード。"
   "ステータス名", "string", "ステータスの名称。"


ロールマスターテーブル（fw_role_master）
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
ロールを定義する。

.. csv-table::
   :widths: 20 20 60
   :header: "項目名", "型", "詳細"

   "ロールコード", "string", "ロールを表す一意なコード。"
   "ロール名", "string", "ロールの名称。"


------------------------
 ワークフローの開始
------------------------
シナリオ：業務データを登録しワークフローを開始する。

.. seqdiag::
   :name: seq-wf-init-action

   seqdiag {
      span_height = 10;
              ユーザー; 登録画面; 業務プログラム; WFエンジン; 業務データベース; FWデータベース;

              ユーザー -> 登録画面 [label="「登録」ボタン押下"];
              登録画面  -> 業務プログラム [label="「登録」処理呼び出し"];
              業務プログラム -> WFエンジン [label="WF初期アクション実行"];
      WFエンジン => FWデータベース [label="実行可否判定"];
      WFエンジン  -> WFエンジン [label="ワークフローID発行"];
      WFエンジン => FWデータベース [label="WF情報登録"];
              業務プログラム <-- WFエンジン  [label="WF履歴、ワークフローID"];
              業務プログラム => 業務データベース[label="業務データ登録"];
              登録画面  <-- 業務プログラム;
              ユーザー <-- 登録画面;

              ユーザー [shape=actor]
              登録画面 [color=pink]
              業務プログラム [color=pink]
      WFエンジン [color=palegreen]
              業務データベース [color=pink]
      FWデータベース [color=palegreen]
   }



使用方法
-------------
FWWFManagerインターフェイスの変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。

.. code-block:: java
    :emphasize-lines: 2

    @Inject
    private FWWFManager wfManager;


初期アクション処理は引数にアクションコードを指定し実行する。 **Handywedge** にて自動登録したワークフロー履歴情報が返却される。
当該情報内には **Handywedge** が採番したワークフローIDを保有しており、業務側で保有する。

.. code-block:: java
    :emphasize-lines: 6

    // ■WF処理：ワークフロー処理（アクション）の実行
    //   初期アクション実行
    //   WFアクションの実行メソッドである#doInitAction()の引数には、アクションコードを指定する
    //   #doInitAction()の返り値はWFエンジンにて登録した履歴オブジェクト（FWWFLog）を返却。
    //   当該オブジェクト内に採番したWFIDがあり、業務側で保有すること。
    FWWFLog wfLog = wfManager.doInitAction(actionCode);

    // 業務データ登録
    Data data = new Data();
    data.setSubject(subject);
    data.setBody(body);
    data.setWfId(wfLog.getWfId()); // 業務データにワークフローIDを保有する
    daoService.insert(data);


-------------------------
アクションリストの取得
-------------------------
シナリオ：業務データの照会画面を表示する。

.. seqdiag::
   :name: seq-wf-action-list

   seqdiag {
      span_height = 20;
              ユーザー; 詳細画面; 業務プログラム; WFエンジン; 業務データベース; FWデータベース;

              ユーザー -> 業務プログラム [label="「詳細画面」表示要求"];
              業務プログラム  -> 業務データベース [label="業務データ取得"];
              業務プログラム -> WFエンジン [label="実行可能アクション取得"];
      WFエンジン => FWデータベース [label="承認系アクション取得"];
              業務プログラム <-- WFエンジン  [label="実行可能なアクションリスト"];
              業務プログラム -> WFエンジン [label="ワークフロー履歴取得"];
      WFエンジン => FWデータベース [label="ワークフロー履歴取得"];
              業務プログラム <-- WFエンジン[label="ワークフロー履歴のリスト"];
              業務プログラム -> 詳細画面 [label="詳細画面生成"];
              詳細画面 -> ユーザー [label="詳細画面表示"];

              ユーザー [shape=actor]
              詳細画面 [color=pink]
              業務プログラム [color=pink]
      WFエンジン [color=palegreen]
              業務データベース [color=pink]
      FWデータベース [color=palegreen]
   }


使用方法
------------
FWWFManagerインターフェイスの変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。

.. code-block:: java
    :emphasize-lines: 2

    @Inject
    private FWWFManager wfManager;


実行可能なアクションは、引数にワークフローIDを指定し取得する。また、ステータス情報、ワークフロー履歴も同様にワークフローIDを指定することで取得できる。

.. code-block:: java
    :emphasize-lines: 3, 7, 11

    // ■WF処理：文書ステータスの取得
    // 文書のステータス情報をWFエンジンより取得
    status = wfManager.getStatus(data.getWfId());

    // ■WF処理：対象文書の履歴リストの取得
    // 業務データに保有しているWFIDを引数に履歴リストを取得する
    wfLogs = wfManager.getWFLogs(data.getWfId());

    // ■WF処理：実行可能なアクションの取得
    // 業務データに保有しているWFIDを引数に実行可能なアクションリスト（承認系＋否認）を取得する
    wfActions = wfManager.getActions(data.getWfId());


承認アクションリストと、否認アクションとを個別に取得することも可能。

.. code-block:: java
    :emphasize-lines: 2

    //実行可能な承認アクションリスト（否認アクションを含まない）のみを取得する場合
    wfActions = wfManager.getGoActions(data.getWfId());


否認アクションはリストの返却ではないことに注意。

.. code-block:: java
    :emphasize-lines: 2

    //実行可能な否認アクションを取得する場合
    wfAction = wfManager.getRollBackAction(data.getWfId());


承認／否認ボタンの画面表示例。実行可能なアクションはリストで **Handywedge** より取得可能な為、画面上ではループさせボタンを表示。
ボタンアクションには、actionCodeを引き渡すように実装する。（承認／否認処理にてactionCodeを使用するため）

.. code-block:: html

    <p:dataList value="#{xxxxx.wfActions}" var="wfAction" type="none" styleClass="border:none" emptyMessage="">
      <p:commandButton
        value="#{wfAction.action}"
        action="#{xxxxx.execute (wfAction.actionCode)}"
      />
    </p:dataList>
 

ワークフロー履歴の画面表示例。

.. code-block:: html

    <p:dataTable
      id="wfLogs"
      var="wfLog"
      value="#{xxxxx.wfLogs}"
      rowKey="#{wfLog.wfSerNo}">

      <f:facet name="header">データ履歴</f:facet>
      <p:column headerText="日時">
        <h:outputText value="#{wfLog.actionDate}">
          <f:convertDateTime  pattern="yyyy/MM/dd HH:mm:ss" timeZone="JST" />
        </h:outputText>
      </p:column>
      <p:column headerText="アクション">
        <h:outputText value="#{wfLog.actionName}" />
      </p:column>
      <p:column headerText="ステータス">
        <h:outputText value="#{wfLog.statusName}" />
      </p:column>
      <p:column headerText="氏名">
        <h:outputText value="#{wfLog.actionOwnerName}" />
      </p:column>
      <p:column headerText="コメント">
        <h:outputText value="#{wfLog.description}" />
      </p:column>
    </p:dataTable>


-----------------
承認・否認処理
-----------------
シナリオ：業務データ照会画面上のワークフローボタン（承認／否認）を押下してワークフローを実行する。

.. seqdiag::
   :name: seq-wf-do-action

   seqdiag {
      span_height = 20;
              ユーザー; 詳細画面; 業務プログラム; WFエンジン; 業務データベース; FWデータベース;

              ユーザー -> 詳細画面 [label=" 「承認／否認」ボタン押下"];
              詳細画面 -> 業務プログラム [label="承認否認処理呼出し"];
              業務プログラム -> WFエンジン [label="対象アクション実行可否判定"];
      WFエンジン => FWデータベース [label="実行可否判定"];
              業務プログラム <-- WFエンジン;
              業務プログラム -> WFエンジン [label="ワークフローアクション実行"];
      WFエンジン => FWデータベース [label="ワークフロー履歴情報登録"];
              業務プログラム <-- WFエンジン[label="ワークフロー履歴"];
              業務プログラム => 業務データベース [label="詳細画面生成"];
              詳細画面 <-- 業務プログラム;
              ユーザー <-- 詳細画面;

              ユーザー [shape=actor]
              詳細画面 [color=pink]
              業務プログラム [color=pink]
      WFエンジン [color=palegreen]
              業務データベース [color=pink]
      FWデータベース [color=palegreen]
   }


使用方法
------------
FWWFManagerインターフェイスの変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。

.. code-block:: java
    :emphasize-lines: 2

    @Inject
    private FWWFManager wfManager;


アクション実行は引数にワークフローID、及びWFボタン押下より取得したアクションコードを指定し実行する。

.. code-block:: java
    :emphasize-lines: 4, 8

    // ■WF処理：指定アクションの実行判定
    // 業務データに保有しているWFID、及びWFボタン押下により取得したアクションIDを引数に実行判定を行う
    // 実行不可の場合、FWWFExceptionがthrowされる
    FWWFAction wfAction = wfManager.checkAction(data.getWfId(), actionCode);

    // ■WF処理：指定アクションの実行
    // WFアクションを実行し（WFエンジン内に保有しているステータスを進める）、またWF履歴を書き込みます
    FWWFLog wfLog = wfManager.doAction(wfAction);

    // 業務処理更新処理
    // ・・・

----------------------
ワークフローエディタ
----------------------
| ワークフロー機能で利用する **ワークフロールート** および **ステータスマスター** の2テーブルのデータの作成支援を行うためのビジュアルエディターがある。
| 下記のHandywedgeのサイトから利用することが可能。
| 詳しい使い方についてはエディタ内の使い方を参照すること。
| |location_link|

.. |location_link| raw:: html

   <a href="/wfeditor.php" target="_blank">ワークフローエディタ</a>
