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

--------------------------------
ライブラリ使用方法
--------------------------------

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


