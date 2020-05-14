プッシュ通知
=============
| プッシュ通知サービスはサーバーアプリケーションからプッシュ通知コンテナに接続されたクライアントへ通知を送信するサービスである。
| サービスに接続するためには予め :doc:`/restservice/resttoken` で発行した認証トークンが必要となる。
| 通知はWebsocketを使って行われるが、サーバーからクライアントへの片方向のメッセージングとする。
| また、プッシュ通知サービスはマルチインタンスでは動作しないため1つのインスタンスで稼働させること。

| シーケンスを次に示す。

.. seqdiag::
  :name: seq-pushnotice

  seqdiag {
    span_height = 10;
      業務プログラム; プッシュ通知ライブラリ; プッシュ通知コンテナ; ユーザー;

      === 接続中ユーザー一覧取得 ===
        ユーザー --> プッシュ通知コンテナ [label="Websocket接続"];
        プッシュ通知コンテナ -> プッシュ通知コンテナ [label="トークン認証"]
        業務プログラム -> プッシュ通知ライブラリ [label="getLoginUsers"];
        プッシュ通知ライブラリ -> プッシュ通知コンテナ [label="GET"];
        プッシュ通知ライブラリ <-- プッシュ通知コンテナ [label="ユーザー一覧"];
        業務プログラム <-- プッシュ通知ライブラリ;

      === プッシュ通知 ===
        業務プログラム -> プッシュ通知ライブラリ [label="sendMessage(userId, message)"];
        プッシュ通知ライブラリ -> プッシュ通知コンテナ [label="POST message"];
        プッシュ通知コンテナ --> ユーザー [label="プッシュ通知 message"];
        プッシュ通知ライブラリ <-- プッシュ通知コンテナ;
        業務プログラム <-- プッシュ通知ライブラリ;

      業務プログラム [color=pink]
      プッシュ通知ライブラリ [shape=circle]
      プッシュ通知コンテナ [color=palegreen]
      ユーザー [shape=actor]
  }

================================
Dockerコンテナについて
================================
| プッシュ通知サービスはDockerコンテナ上のTomcatで稼働している。
| Tomcatのホームディレクトリは

.. code-block:: none

  /usr/local/tomcat

| となる。
| プッシュ通知サービスのログファイルはTomcatホームディレクトリの ``logs`` ディレクトリに出力される。
|

| dockerコンテナを作成する前にマウントするディレクトリを決定する。
| 本ガイドでは

::

  /var/docker/hw-push-notice/
  
| に設定し、docker-composeを使ったコンテナ起動について記載する。

docker load
---------------
コンテナのイメージファイルは以下のURLにある。

::

  https://docker.handywedge.com/images/push_notice/handywedge-push_notice.tgz

リポジトリに取り込むには以下のコマンドを実行する。

::

  curl https://docker.handywedge.com/images/push_notice/handywedge-push_notice.tgz | docker load

``handywedge/push_notice:master`` としてイメージが取り込まれる。


docker-compose.yml
------------------------------

コンテナを起動するためのdocker-compose.ymlサンプル。

.. code-block:: yaml

  version: '3.7'

  services:

    hw-push-notice:
      container_name: hw-push-notice
      image: handywedge/push_notice:master
      ports:
        - "18080:8080"
      networks:
        hw-net:
      volumes:
        - /var/docker/hw-push-notice/resources/:/usr/local/tomcat/resources/
        - /var/docker/hw-push-notice/conf/:/usr/local/tomcat/conf/Catalina/localhost/
      restart: always

  networks:
    hw-net:
      driver: bridge

.. important:: マウントディレクトリ配下にresourcesとconfのディレクトリを設定すること。

hw-pushnotice.xml
------------------------------
| confディレクトリに **hw-pushnotice.xml** を作成し **hw-pushnotice.properties** に設定したデータソースを定義する。
| プロパティを反映させるためにクラスローダーの委譲設定も記載する。

::

  <?xml version='1.0' encoding='utf-8'?>
  <Context>
    <Loader delegate="true" />
    <Resource
      name="jdbc/fw"
      auth="Container"
      type="javax.sql.DataSource"
      driverClassName="org.postgresql.Driver"
      url="jdbc:postgresql://handywedge-db:5432/handywedge"
      username="handywedge-app"
      password="handywedge-app-pswd"
      initialSize="2"
      maxWaitMillis="10000" />
  </Context>

hw-pushnotice.properties
------------------------------
resourcesディレクトリに **hw-pushnotice.properties** を作成し必要な設定をする。

::

  # 認証時にHWのテーブル接続に使うためのデータソース名
  JDBC_JNDI_NAME=jdbc/fw

  # Websocket接続のPING送信間隔（秒）
  PING_INTERVAL_SEC=45

  # サーバー間で利用する認証キー
  ACCESS_KEY=HW-PUSH

  # Websocketの同一ユーザーからの複数接続設定
  MULTI_SESSION=true

================================
サーバーライブラリ使用方法
================================
サーバーサイドのライブラリを使用するにはマイクロサービスに設定したサーバー間認証で利用する認証キーが必要となる。

.. hint:: hw-pushnotice.propertiesのACCESS_KEY

Maven
-----------------

::

  <dependency>
    <groupId>com.handywedge</groupId>
    <artifactId>handywedge-pushnotice-client</artifactId>
    <version>0.9.0-SNAPSHOT</version>
  </dependency>

getLoginUsers
-----------------
| 現在コンテナに接続中のユーザー一覧を取得する。
| 戻り値はユーザーIDのリストとなる。

.. code-block:: java

  import com.handywedge.pushnotice.client.PushClient;

  // addressはマイクロサービスのエンドポイント、keyはマイクロサービスに埋め込んだサーバー間認証キー
  List<String> connectUsers = PushClient.getLoginUsers(address, key).getLoginUsers();

sendMessage
-----------------
| 指定したユーザーにpush通知を送信する。

.. code-block:: java

  import com.handywedge.pushnotice.client.PushClient;

  // addressはマイクロサービスのエンドポイント、keyはマイクロサービスに埋め込んだサーバー間認証キー
  // userIdは通知を送りたいユーザーID、messageは送信するメッセージ
  PushClient.sendMessage(address, key, userId, message);

================================
クライアントからの接続方法
================================
下記Websocket接続URLで接続する。

``ws(wss)://{マイクロサービスコンテナエンドポイント}//hw-pushnotice/Ws/pushnotice/{認証トークン}``

