アプリケーション情報機能
=========================
.. index::
   single: コンテキスト情報
   single: ロケール
   single: メッセージリソース
   pair: FWContext; インターフェイス
   pair: fwMsgResources; 暗黙オブジェクト

-----------------
コンテキスト情報
-----------------
コンテキスト情報としてログインしているユーザーに関する情報やリクエストに関する情報を提供する。

リクエスト情報
--------------------------
コンテキスト情報機能で提供しているリクエスト情報の項目を :numref:`tab-request-info` 示す。

.. csv-table:: リクエスト情報
   :name: tab-request-info
   :header: "項目名", "内容"
   :widths: 25 75

   "リクエストID", "リクエスト毎に一意になるID"
   "リクエストURL", "リクエストURL（クエリーストリングは含まない）"
   "リクエスト開始時刻", "リクエストを受け付けた時刻"
   "最終アクセス時刻", "前回のリクエストを受け付けた時刻"
   "サーバー名", "リクエストを処理しているサーバー名"
   "アプリケーションID", "アプリケーションを特定するID"
   "コンテキストパス", "アプリケーションのコンテキストパス"
   "APIトークン", "リクエストで認証されたAPIトークン"


.. _login-user-info:

ログインユーザー情報
--------------------------
コンテキスト情報機能で提供しているログインユーザー情報の項目を :numref:`tab-user-info` 示す。

.. csv-table:: ログインユーザー情報
   :name: tab-user-info
   :header: "項目名", "内容"
   :widths: 25 75

   "ユーザーID", "ユーザーを識別する一意なID"
   "ユーザー名", "ユーザー名"
   "ロケール情報", "通常はDBに登録されているロケール情報が設定される（アプリケーションで上書き可能）"
   "最終ログイン時間", "最終ログインの時刻（ログイン後に当該項目を取得した場合、今回のログイン時刻が取得可）"
   "前回ログイン時間", "前回（１つ前）のログインした時刻"
   "ロールコード", "ユーザーに設定されているロールID"
   "ロール名", "ユーザーに設定されているロール名"

.. .. tip:: 本情報はログイン中のユーザー情報として「コンテキスト情報」にて取得可能な項目。
　

使用方法
--------
FWContextインターフェイス型の変数を定義し＠Injectアノテーションを付けてオブジェクトを注入する。

.. code-block:: java
   :emphasize-lines: 2, 3

   // FWContextインターフェイスの変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。
   @Inject
   private FWContext context;


注入されたオブジェクトを使用してコンテキスト情報を取得する。

.. code-block:: java
   :emphasize-lines: 2

   // 注入されたオブジェクトを使用してコンテキスト情報を取得する。
   FWUser user = context.getUser();
   String loginId = user.getId ();


.. _message-resource:

----------------------
メッセージリソース
----------------------
**Handywedge** では予め登録してあるメッセージをキーで索引できるメッセージリソース機能を提供する。

メッセージは基底名をアプリケーションIDとするプロパティファイルに記述する。

.. hint:: アプリケーションIDが「handywedge-sample」の場合、プロパティファイル名は **handywedge-sample.properties** となる。

国際化対応
-----------
メッセージリソースでは国際化に対応しており、ログインしているユーザーのコンテキスト情報で指定されたロケールに応じたメッセージを取得できる。

メッセージリソースはロケール毎に定義する。

.. csv-table:: アプリケーションIDが「handywedge-sample」の場合
   :name: property-locale
   :header: "ロケール", "プロパティファイル名", "メッセージ設定例"

   "日本(ja)", "handywedge-sample **_ja.properties** ", "welcome.message=テストアプリへようこそ！"
   "米国(en)", "handywedge-sample **_en.properties** ", "welcome.message=Hello test app!"


使用可能なロケールについては、 `サポートされているロケール <https://docs.oracle.com/javase/jp/6/technotes/guides/intl/locale.doc.html>`_ を参照。


プロパティファイルの配置
-----------------------------
プロパティファイルはwarファイルの「WEB-INF/classes」ディレクトリに配備する。

warファイルのプロパティ配置例を  :numref:`tab-war-property` に示す。

.. code-block:: text
   :name: tab-war-property
   :caption: warファイルのプロパティ配置例

   handywedge-sample.war
   ├── WEB-INF
   │ ・・・                 　　　　　　　　　　　　　　　　　　 ※プロパティファイルは、ルート直下に配置
   │ ├── classes
   │ │ ├── handywedge-sample_en.properties　システムプロパティ（ロケール米国）
   │ │ ├── handywedge-sample_ja.properties　システムプロパティ（ロケール日本）
   │ │ ├── handywedge-sample.properties システムプロパティ
   │ ・・・
   …


.. todo::　FWプロパティの記述を別に分ける。

   .. FWプロパティ
   ..  -------
   .. 「システムプロパティ」には**Handywedge** 設定値として指定の項目が存在する。
   .. **Handywedge** の各設定値については、別紙「FWコンフィグガイド」を参照。

使用方法
-----------
FWMessageResourceインターフェイス型の変数に＠Injectアノテーションを付けてオブジェクトを注入し、注入されたオブジェクトからキーを指定してメッセージを取得する。

.. code-block:: java
   :emphasize-lines: 2,3,6

    // FWMessageResourceインターフェイスの変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。
    @Inject
    private FWMessageResources resources;

    // FWMessageResourceからメッセージを取得する。
    message = resources.get("welcome.message ");


JSFでは暗黙オブジェクト **[fwMsgResources]** を使ってメッセージを取得する。

.. code-block:: html
   :emphasize-lines: 1

    <h:outputText value="#{fwMsgResources.get('welcome.message')}" />

