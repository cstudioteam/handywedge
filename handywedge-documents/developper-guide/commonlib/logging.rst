ロギング機能
==============
.. index::
   single: log4j2.xml
   pair: slf4j; ロギングファサード

----------
ロギング
----------
| ビジネスロジックの入口・出口、SQLの実行、例外の発生など主要な場所ではフレームワークが自動的にログを出力するが、アプリケーションは必要に応じて任意でログを出力することができる。
| ログ出力は拡張性を持たせるためにロギングファサード **slf4j** を使って出力する。そのため、アプリケーション開発者はロガーの実装クラスを意識せずにログを出力する。

設定方法
------------
ログの出力制御は **log4j2.xml** などで設定を行う。

ログレベル
~~~~~~~~~~~~~
出力するログレベルを :numref:`tab-log-level` に示す。

.. list-table:: ログレベル
   :name: tab-log-level
   :header-rows: 1

   * - レベル
     - 内容
     - 出力内容の基準
   * - error
     - エラー
     - | 継続不可能な問題が発生した場合、その内容と問題を解決するために
       | 必要な情報を出力する。
   * - warn
     - 警告
     - | 問題が発生したが継続可能な場合、その内容と問題を解決するために
       | 必要な情報を出力する。
   * - info
     - 情報
     - | 設定ファイル等を読み込んだときの内容や、特にログとして出力しておき
       | たい情報を出力する。リクエストおよびビジネスロジックメソッドの開始、
       | 終了の情報はフレームワークで自動的にログ出力する。
   * - debug
     - デバッグ
     - | アプリケーション開発者がデバッグに必要な情報を出力する。
       | （外部クラスから呼ばれるメソッドの引数の内容や戻り値等）
   * - trace
     - トレース
     - より詳細なデバッグ情報を出力する。


ログ出力カテゴリ
~~~~~~~~~~~~~~~~~~~~~~~
出力内容により次のカテゴリに分かれる。

.. csv-table:: ログカテゴリ
   :name: tab-log-category
   :header-rows: 1

   "カテゴリ", "カテゴリ内容"
   "FW", "フレームワークが出力するログ"
   "AP", "業務アプリケーションが出力するログ"
   "PERF", "処理時間計測ログ"
   "RESP", "リクエスト処理時間計測ログ（フレームワークが出力するログ）"
   "JDBC", "JDBC関連ログ"


-----------------
SQLトレース
-----------------
| トランザクションの開始・終了、コミット・ロールバック、例外発生についてはフレームワークが自動的にログ出力。
| また、JDBCドライバが発行するSQLは、log4jdbc（JDBCドライバをラップしログ出力するライブラリ）を使用しログ出力する。

設定方法
------------
ロガー log4j2を使用したSQLトレースの設定例を :numref:`code-log4j2-xml-sample` 示す。

.. code-block:: xml
   :name: code-log4j2-xml-sample
   :caption: log4j2.xml（設定例）

   <Logger name="jdbc.connection" level="warn" additivity="false">
      <AppenderRef ref="${app_id}_jdbc" />
   </Logger>

   <Logger name="jdbc.sqltiming" level="debug" additivity="false">
      <AppenderRef ref="${app_id}_jdbc" />
   </Logger>

   <Logger name="jdbc.sqlonly" level="error" additivity="false">
      <AppenderRef ref="${app_id}_jdbc" />
   </Logger>

   <Logger name="jdbc.audit" level="warn" additivity="false">
      <AppenderRef ref="${app_id}_jdbc" />
   </Logger>

   <Logger name="jdbc.resultset" level="warn" additivity="false">
      <AppenderRef ref="${app_id}_jdbc" />
   </Logger>


出力イメージ
~~~~~~~~~~~~~~
出力フォーマットは同じく **log4j2.xml** にて設定する。出力カテゴリは「JDBC」。

::

   2017-08-16 09:56:13.019 DEBUG [userid] [JDBC] requestId jdbc.sqltiming org.apache.tomcat.dbcp.dbcp2.DelegatingStatement.executeQuery(DelegatingStatement.java:206)
   3. SELECT * FROM xxxxx ORDER BY id ASC
   {executed in 2 msec}


---------------
デバッグログ
---------------
アプリケーションは必要に応じて任意でログを出力することができる。使用方法を以下に示す。

使用方法
---------
Loggerインターフェイスの変数を定義し、＠Injectアノテーションを付加してオブジェクトを注入する。

.. code-block:: java
   :emphasize-lines: 2

    @Inject
    private FWLogger logger;

    public String method(String key) {
        String value = msg.get(key);
        logger.debug("method() key={}, value={}", key, value);
    }


出力イメージ
~~~~~~~~~~~~~~
出力カテゴリは「AP」となる。（Logger#info()、debug()など、いずれのメソッドを利用しても自動的に割り当てられる）

::

   2017-08-16 15:15:08.244 DEBUG [userid] [AP] requestId  呼出し元クラス method() key=XXXXX, value=XXXXX

 
--------------
性能トレース
--------------
アプリケーションは必要に応じて任意で処理に掛かる性能トレースログを出力することができる。使用方法を以下に示す。

使用方法
----------
ある処理（メソッド）における処理時間を出力する場合の例。


.. code-block:: java
   :emphasize-lines: 2

    @Inject
    private FWLogger logger;

    public void methodA() {
        long startTime = logger.perfStart("methodA"); // 引数にメソッド名を指定。返り値としてlong値を受ける
        // ：
        // （業務処理）
        // ：
        logger.perfEnd ("methodA", startTime); // long値を引数に指定する
    }


出力イメージ
~~~~~~~~~~~~~~
＜使用方法＞で示したコードに応じたログ出力例。Endログに経過時間を出力。出力カテゴリは「PERF」となる。

::

   2017-08-16 15:14:54.490 INFO [userid] [PERF] requestId  呼出し元クラス methodA() start.
   ・・・
   2017-08-16 15:14:54.493 INFO [userid] [PERF] requestId  呼出し元クラス methodA() end. ElapsedTime[3]ms
 
