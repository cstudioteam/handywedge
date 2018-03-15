RESTトークン
================
.. index::
   single: 認証トークン

.. _token-issue:

-----------------
認証トークン発行
-----------------
**Handywedge** は、パスワード認証にてREST APIを利用するためのトークンを発行する機能を提供する。
事前に登録済みのユーザー情報（ID、パスワード）による認証を行い、トークンを発行する。（RESTによるログイン機能をイメージ）


使用方法
-----------
本機能はJSON形式POSTリクエストにより実行する。

.. seqdiag::
   :name: seq-token-issue

   seqdiag {
      span_height = 20;

      === トークン発行処理（ここから） ===
      RESTクライアント -> RESTフィルタ [label="トークン発行要求"];
      RESTフィルタ  -> トークン発行API [label="トークン発行要求"];
              トークン発行API -> トークン発行API [label="パスワード認証"];
      RESTフィルタ  <-- トークン発行API [label="トークン"];
      RESTクライアント <-- RESTフィルタ [label="トークン"];
      === トークン発行処理（ここまで） ===
      RESTクライアント -> RESTフィルタ [label="GET/POST/PUT/DELETE"];
      RESTフィルタ  -> RESTフィルタ [label="トークン認証"];
      RESTフィルタ  => RESTアプリケーション [label="GET/POST/PUT/DELETE"];
      RESTクライアント <-- RESTフィルタ [label="レスポンス"];

      RESTクライアント [color=pink]
      RESTアプリケーション [color=pink]
      RESTフィルタ [color=palegreen]
              トークン発行API [color=palegreen]
   }


インターフェース
~~~~~~~~~~~~~~~~
:URL（エンドポイント）:
   /fw/rest/api/token/pub
:メソッド:
   POST



送受信サンプル
~~~~~~~~~~~~~~
 リクエスト:

 .. code-block:: json

   {"id":"inte-rest-api", "password":"hogehoge"}


 レスポンス:

 .. code-block:: json

   {"return_cd":0,"token":"b6ed000a36fe7c546a92d0558ba9d369"}


ヘッダー
~~~~~~~~~
.. csv-table::
   :widths: 20 30 50
   :header: "項目名", "値", "詳細"

   "Content-Type", "application/json", "必須。"


パラメーター
~~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "id", "string", "必須。"
   "password", "string", "必須。"
   "multiple", "int", "| 0以下：以前のトークンは削除してトークンを発行。
   | 1以上：以前のトークンは残したままトークンを発行。（省略時は0）"


戻り値（正常時）
~~~~~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "return_cd", "int", "正常終了は0固定。"
   "token", "string", ""


戻り値（例外時）
~~~~~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "return_cd", "int", "負の値。"
   "return_msg", "string", ""


例外内容
~~~~~~~~
.. csv-table::
   :widths: 10 45 45
   :header: "return_cd", "return_msg", "内容"

   "-9000", "予期しないエラーが発生しました。サーバー管理者に確認して下さい。エラーメッセージ[値]", "予期しないエラーが発生。値には例外インスタンスから取得したメッセージ。"
   "-9003", "認証エラーです。ユーザーIDとパスワードを確認して下さい。", "ユーザーIDが存在しないか、パスワードが一致しない。"
   "-9004", "パラメータのユーザーIDもしくはパスワードがありません。", "リクエストパラメータが不足。"


.. _token_delete:

-----------------
認証トークン削除
-----------------
**Handywedge** は、「 :ref:`token-issue` 」で発行したトークンを削除する機能を提供する。

「 :ref:`token-issue` 」によるRESTログイン後のログアウト機能。

使用方法
--------
本機能はJSON形式DELETEリクエストにより実行する。

インターフェース
~~~~~~~~~~~~~~~~~
:URL（エンドポイント）:
   /fw/rest/api/token/delete
:メソッド:
   DELETE


送受信サンプル
~~~~~~~~~~~~~~~~
 リクエスト:

 .. code-block:: text

     なし


 レスポンス:

 .. code-block:: json

   {"return_cd":0}


ヘッダー
~~~~~~~~~~
.. csv-table::
   :widths: 20 30 50
   :header: "項目名", "値", "詳細"

   "Content-Type", "application/json", "必須。"
   "Authorization", "Bearer token", "| 必須。tokenはログインAPIで発行されたtoken。
   | Bearerとtokenの間は半角スペース1つ。"


パラメーター
~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "なし", "", ""


戻り値（正常時）
~~~~~~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "return_cd", "int", "正常終了は0固定。"

戻り値（例外時）
~~~~~~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "return_cd", "int", "負の値。"
   "return_msg", "string", ""

例外内容
~~~~~~~~
.. csv-table::
   :widths: 10 45 45
   :header: "return_cd", "return_msg", "内容"

   "-9000", "予期しないエラーが発生しました。サーバー管理者に確認して下さい。エラーメッセージ[値]", "予期しないエラーが発生。値には例外インスタンスから取得したメッセージ。"


.. _token-validate:

----------------------------
認証トークン有効性チェック
----------------------------
**Handywedge** は、「 :ref:`token-issue` 」で発行したトークンの有効性をチェックする機能を提供する。

使用方法
-----------
本機能はJSON形式GETリクエストにより実行する。

インターフェース
~~~~~~~~~~~~~~~~
:URL（エンドポイント）:
   /fw/rest/api/token/validate
:メソッド:
   GET


送受信サンプル
~~~~~~~~~~~~~~~~
 リクエスト:

 .. code-block:: text

     なし


 レスポンス:

 .. code-block:: json

   {"return_cd":0}


ヘッダー
~~~~~~~~~
.. csv-table::
   :widths: 20 30 50
   :header: "項目名", "値", "詳細"

   "Content-Type", "application/json", "必須。"
   "Authorization", "Bearer token", "| 必須。tokenはログインAPIで発行されたtoken。
   | Bearerとtokenの間は半角スペース1つ。"

パラメーター
~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "なし", "", ""

戻り値（正常時）
~~~~~~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "return_cd", "int", "正常終了は0固定。"

戻り値（例外時）
~~~~~~~~~~~~~~~~~
.. csv-table::
   :widths: 25 15 60
   :header: "項目名", "型", "詳細"

   "return_cd", "int", "負の値。"
   "return_msg", "string", ""

例外内容
~~~~~~~~~~
.. csv-table::
   :widths: 10 45 45
   :header: "return_cd", "return_msg", "内容"

   "-9000", "予期しないエラーが発生しました。サーバー管理者に確認して下さい。エラーメッセージ[値]", "予期しないエラーが発生。値には例外インスタンスから取得したメッセージ。"
