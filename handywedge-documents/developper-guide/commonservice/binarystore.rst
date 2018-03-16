バイナリーストア
==================
Amazon S3バイナリーサービスを例にしてバイナリーストアのシーケンスを次に示す。

.. seqdiag::
  :name: seq-wf-init-action

  seqdiag {
     span_height = 10;
             ユーザー; アップロード画面; 業務プログラム; BinaryStoreコンテナ; LocalFile; S3;

     === POST ===
             ユーザー -> アップロード画面 [label="POST"];
             アップロード画面 -> 業務プログラム [label=""];
             業務プログラム -> BinaryStoreコンテナ [label="UPLOAD requestId, filename, file"];
     BinaryStoreコンテナ => LocalFile [label="WRITE"];
     BinaryStoreコンテナ => LocalFile [label="READ"];
     BinaryStoreコンテナ => S3 [label="POST bucketname, filename, file"];
     BinaryStoreコンテナ => LocalFile [label="DELETE"];
     業務プログラム <-- BinaryStoreコンテナ [label=""];
             アップロード画面 <-- 業務プログラム [label=""];
             ユーザー <-- アップロード画面 [label=""];

     === GET ===
             ユーザー -> 業務プログラム [label="GET"];
             業務プログラム -> BinaryStoreコンテナ [label="GET requestId, filename"];
     BinaryStoreコンテナ => S3 [label="GET bucketname, filename"];
     業務プログラム <-- BinaryStoreコンテナ [label="時限URL"];
             ユーザー <-- 業務プログラム [label="時限URL"];
             ユーザー => S3 [label="GET"];

             ユーザー [shape=actor]
     LocalFile [shape=circle]
             アップロード画面 [color=pink]
             業務プログラム [color=pink]
     BinaryStoreコンテナ [color=palegreen]

     group {
       color=khaki

                  アップロード画面; 業務プログラム;
     }

     group {
       color=gainsboro

       BinaryStoreコンテナ; LocalFile;
     }

     group {
       color=azure

       S3;
     }
  }

-----------------
インターフェース
-----------------
バイナリーストアは以下の4つのエンドポイントを提供する。

#. upload
#. get
#. list
#. delete

upload
-----------

:URL（エンドポイント）:
  /handywedge-binarystore/store/service/binary/upload
:メソッド:
  POST
:送信方法:
  multipart/form-data

form-dataパラメーター
^^^^^^^^^^^^^^^^^^^^^^^^

.. csv-table::
  :header: "項目名", "content-type", "詳細"

  "json", "text/plain", "必須。"
  "file", "*ファイルに準拠*", "必須。"

.. csv-table:: jsonフィールド
  :header: "プロパティ", "型", "詳細"

  "requestId", "string", "必須。bucketName省略時はバケット名となる。"
  "bucketName", "string", "バケット名。"

戻り値
^^^^^^^^^^^^^^

.. csv-table::
  :header: "項目名", "型", "詳細"

  "return_cd", "int", "正常終了は0固定。"
  "binaryInfos", "binaryinfo[]", "アップロードされたファイルの情報。 :numref:`binaryinfo`。"

.. _binaryinfo:
.. csv-table:: binaryinfo
  :header: "項目名", "型", "詳細"

  "bucketName", "string", "バケット名。"
  "fileName", "string", "ファイル名。（path/to/fileのようにパス付の場合もあり）"
  "size", "long", "ファイルサイズ（バイト）。"
  "contentType", "string", "コンテントタイプ。"
  "url", "string", "ダウンロードURL（恒久）"
  "presignedUrl", "string", "ダウンロードURL（時限）。30分有効。"

get
-------

:URL（エンドポイント）:
  /handywedge-binarystore/store/service/binary/get
:メソッド:
  POST

パラメーター
^^^^^^^^^^^^^^^

.. csv-table::
  :header: "項目名", "型", "詳細"

  "requestId", "string", "必須。bucketName省略時はバケット名となる。"
  "bucketName", "string", "バケット名。"
  "fileName", "string", "ファイル名。（path/to/fileのようにパス付も可）"

戻り値
^^^^^^^^^^^^^^

.. csv-table::
  :header: "項目名", "型", "詳細"

  "return_cd", "int", "正常終了は0固定。"
  "binaryInfos", "binaryinfo[]", "ファイルの情報。 :numref:`binaryinfo`。"

list
---------
:URL（エンドポイント）:
  /handywedge-binarystore/store/service/binary/list
:メソッド:
  POST

パラメーター
^^^^^^^^^^^^^^^

.. csv-table::
  :header: "項目名", "型", "詳細"

  "requestId", "string", "必須。bucketName省略時はバケット名となる。"
  "bucketName", "string", "バケット名。"

戻り値
^^^^^^^^^^^^^^

.. csv-table::
  :header: "項目名", "型", "詳細"

  "return_cd", "int", "正常終了は0固定。"
  "binaryInfos", "binaryinfo[]", "ファイルの情報。 :numref:`binaryinfo`。"

delete
----------
:URL（エンドポイント）:
  /handywedge-binarystore/store/service/binary/delete
:メソッド:
  POST

パラメーター
^^^^^^^^^^^^^^^

.. csv-table::
  :header: "項目名", "型", "詳細"

  "requestId", "string", "必須。bucketName省略時はバケット名となる。"
  "bucketName", "string", "バケット名。"

戻り値
^^^^^^^^^^^^^^

.. csv-table::
  :header: "項目名", "型", "詳細"

  "return_cd", "int", "正常終了は0固定。"
  "binaryInfos", "binaryinfo[]", "ファイルの情報。 :numref:`binaryinfo`。"

例外について
------------
例外が発生した場合は、例外に応じたHTTPステータスコードでレスポンスが返る。

----------------------
Dockerコンテナについて
----------------------
| バイナリーストアサービスはDockerコンテナ上のTomcatで稼働している。
| Tomcatのホームディレクトリは

.. code-block:: none

  /usr/local/tomcat

| となる。
| バイナリーストアサービスのログファイルはTomcatホームディレクトリの ``logs`` ディレクトリに出力される。
|

--------------
Amazon S3
--------------
Amazon S3と連携するためのバイナリーストアサービスのDockerコンテナについて記述する。

docker load
-------------
コンテナのイメージファイルは以下のURLにある。

.. code-block:: none

  https://docker.handywedge.com/images/binarystore/aws/handywedge-binarystore-aws.tgz

リポジトリに取り込むには以下のコマンドを実行する。

.. code-block:: none

  curl https://docker.handywedge.com/images/binarystore/aws/handywedge-binarystore-aws.tgz | docker load

docker run
------------------
コンテナの起動は以下のコマンドを実行する。

.. code-block:: none

  docker run -d -i -p 8082:8080 -e LOG_LEVEL=info -e AWS_ACCESS_KEY_ID=XXXXX -e AWS_SECRET_KEY=XXXXX --name handywedge-binarystore handywedge/binarystore/aws:master

.. csv-table::
  :header: "オプション", "説明"

  "-p", "サービスは8080ポートで待ち受けしているので環境に合わせてフォワード設定をする。"
  "-e LOG_LEVEL", "ログレベルの設定。省略時はinfoとなる。設定できる値は[fatal, error, warn, info, debug, trace]。"
  "-e AWS_ACCESS_KEY_ID, -e AWS_SECRET_KEY", "Amazon S3に接続するための認証情報を設定する。"
  "--name", "起動するコンテナ名。"
