認証機能
==========
.. index::
   single: 認証トークン
   pair: FWLoginManager; インターフェイス

-------------
ログイン認証
-------------
**Handywedge** は、事前に登録済みのユーザー情報（ID、パスワード）を用いて認証を行う機能を提供する。
また、ログイン後は **Handywedge** にてログイン中のユーザー情報を保有し、 :ref:`login-user-info` にて取得することができる。

使用方法
--------
.. seqdiag::
   :name: seq-login

   seqdiag {
      span_height = 20;
              ユーザ; FWSessionFilter; 業務プログラム; ログインページ; FWLoginManager; ユーザマスタ;

              ユーザ -> FWSessionFilter [label="業務プログラムへのリクエスト"];
      FWSessionFilter -> ログインページ [label="", rightnote="ログインしていないのでログインページへリダイレクト"];
              ユーザ <-- ログインページ [label="ログイン画面"];
              ユーザ -> ログインページ [label="GET （ID、パスワード）"];
              ログインページ -> FWLoginManager [label="login(id, password)"];
      FWLoginManager => ユーザマスタ [label="SELECT"];
              ログインページ <- FWLoginManager [label="照合結果"];
              ユーザ <- ログインページ [label=""];
              ユーザ -> FWSessionFilter [label="業務プログラムへのリクエスト"];
      FWSessionFilter => 業務プログラム [label=""];
              ユーザ <- FWSessionFilter [label="業務プログラムのレスポンス"];

              ユーザ [shape=actor]
              ユーザマスタ [shape=circle, color=palegreen]
              ログインページ [color=pink]
              業務プログラム [color=pink]
      FWSessionFilter [color=palegreen]
      FWLoginManager [color=palegreen]
   }


FWLoginManagerインターフェイスの変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。

.. code-block:: java
   :emphasize-lines: 2

    @Inject
    private FWLoginManager loginMgr;


ログイン
~~~~~~~~
FWLoginManagerのオブジェクトのloginメソッドを実行する。

戻り値がtrueの場合、ログイン成功となる。

.. code-block:: java
   :emphasize-lines: 1

    boolean auth = loginMgr.login(id, password);  // 認証された場合はtrueを返し、同時にセッションのユーザー情報に値を設定
    if (auth) {
        // 認証OK時の処理
    } else {
        // 認証NG時の処理
    }


ログアウト
~~~~~~~~~~
FWLoginManagerのオブジェクトのlogoutメソッドを実行する。

.. code-block:: java
   :emphasize-lines: 1

    loginMgr.logout();  // セッションからユーザー情報を削除
