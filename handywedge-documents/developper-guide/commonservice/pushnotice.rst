プッシュ通知
=============
| プッシュ通知サービスはサーバーからプッシュ通知コンテナに接続されたクライアントへ通知を送信するサービスである。
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
        業務プログラム -> プッシュ通知ライブラリ [label="sendMessage(message)"];
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


:docker-compose.yml:

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



================================
ライブラリ使用方法
================================

getLoginUsers
-----------------
| 現在コンテナに接続中のユーザー一覧を取得する。
| 戻り値はユーザーIDのリストとなる。

.. code-block:: java

  import com.handywedge.pushnotice.client.PushClient;

  // addressはマイクロサービスのエンドポイント、keyはマイクロサービスに埋め込んだ接続key
  List<String> connectUsers = PushClient.getLoginUsers(address, key).getLoginUsers();

sendMessage
-----------------
| 指定したユーザーにpush通知を送信する。

.. code-block:: java

  import com.handywedge.pushnotice.client.PushClient;

  // addressはマイクロサービスのエンドポイント、keyはマイクロサービスに埋め込んだ接続key
  // userIdは通知を送りたいユーザーID、messageは送信するメッセージ
  PushClient.sendMessage(address, key, userId, message);

Maven
-----------------





