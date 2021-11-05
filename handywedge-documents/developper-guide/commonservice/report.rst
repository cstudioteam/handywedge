PDFレポート生成
=================
| PDFレポート生成サービスはJaspersoft Studioで作成したテンプレートをもとにPDFを生成するサービスである。
| サービスを利用するには、予めクラスパス上にテンプレートファイル（.jasper）やfontなどの必要なリソースを配置する必要がある。

-----------------
インターフェース
-----------------
PDFレポート生成サービスは以下の1つのエンドポイントを提供する。

#. POST pdf

POST pdf
----------
:URL（エンドポイント）:
  /handywedge-report-service/v1/pdf
:メソッド:
  POST

パラメーター
^^^^^^^^^^^^^^^
.. csv-table::
  :header: "項目名", "型", "詳細"

  "jasperFileName", "string", "必須。テンプレートファイル名を指定する。"
  "parameters", "object", "テンプレートのparameterを指定する。"
  "fields", "object配列", "テンプレートのfieldを指定する。"

.. code-block:: js

  {
    "jasperFileName": "sample.jasper",
    "parameters": {
      "P1": "parameter1",
      "P2": "パラメータ2",
      "TITLE": "帳票タイトル"
    },
    "fields":[
      {
        "F1": 1,
        "F2": "カラム1"
      },
      {
        "F1": 2,
        "F2": "カラム2"
      },
      {
        "F1": 3,
        "F2": "カラム3"
      }
    ]
  }


戻り値
^^^^^^^^^^^^^^
ファイルダウンロード形式で生成されたPDFがダウンロードされる。
エラーが発生した場合はjsonで返される。

----------------------------------
Dockerコンテナについて
----------------------------------
| PDFレポート生成サービスはDockerコンテナ上のTomcatで稼働している。
| Tomcatのホームディレクトリは

.. code-block:: none

  /usr/local/tomcat

| となる。
| PDFレポート生成サービスのログファイルはTomcatホームディレクトリの ``logs`` ディレクトリに出力される。
| また、 ``/usr/local/tomcat/resources`` にクラスパスが設定されているので、必要に応じてホスト側にマウントして利用する。


docker load
---------------
コンテナのイメージファイルは以下のURLにある。
CPUアーキテクチャは ``amd64`` ``arm64`` の2種類に対応している。

::

  https://docker.handywedge.com/images/report-service/amd64/handywedge-report-service.tgz 
  https://docker.handywedge.com/images/report-service/arm64/handywedge-report-service.tgz 

リポジトリに取り込むには以下のコマンドを実行する。

::

  curl https://docker.handywedge.com/images/report-service/amd64/handywedge-report-service.tgz  | docker load

``handywedge/report-service:master-amd64`` としてイメージが取り込まれる。


docker run 
------------------------------

コンテナを起動するためのdocker runコマンドのサンプル。

.. code-block:: none

  docker run -d -p 8080:8080 -e LOG_LEVEL=debug --name hw-report -v /path/to/jasper:/usr/local/tomcat/resources handywedge/report-service:master-amd64
