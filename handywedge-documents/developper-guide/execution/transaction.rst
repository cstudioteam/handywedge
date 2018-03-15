トランザクション制御
====================
.. index::
   single: トランザクション
   single: コミット
   single: ロールバック
   single: データソース
   pair: @FWTransactional; アノテーション


トランザクションとは永続性記録装置（データベース）への登録、更新、削除などの処理を行う際、整合性を保持するために分割することができない
一連の単位を表す。

トランザクションの管理は **Handywedge** が行う。

----------------------
トランザクション管理
----------------------
トランザクション開始点となるメソッドに **Handywedge** が提供する **@FWTransactionalアノテーション** を付けることで、このメソッドを
トランザクションの境界とし、 **Handywedge** がトランザクションの管理を行う。

**Handywedge** では **@FWTransactionalアノテーション** が付けられたメソッドが正常に終了するとトランザクションをコミットし、
例外またはエラーがスローされた場合にはトランザクションをロールバックする。


正常時のシーケンスを次に示す。

.. seqdiag::
   :name: seq-transaction

   seqdiag {
      span_height = 10;
               業務プログラム（画面）; インターセプター; 業務プログラム; エンティティ; JTA;

              業務プログラム（画面） -> インターセプター [label="業務プログラム実行", rightnote="呼び出し対象の@FWTransactionalアノテーションで自動的に呼び出される"];
              インターセプター => JTA [label="トランザクション開始"];
              インターセプター -> 業務プログラム [label="業務プログラム実行"];
              業務プログラム => エンティティ [label="データ更新"];
              インターセプター <-- 業務プログラム [label="実行結果"];
              インターセプター => JTA [label="コミット"];
              業務プログラム（画面） <- 業務プログラム [label = "実行結果"];

              業務プログラム（画面）[color=pink]
              業務プログラム[color=pink]
              エンティティ [shape=circle, color=pink]
              インターセプター [color=palegreen]
      JTA [color=palegreen]
   }


エラー時のシーケンスを次に示す。

.. seqdiag::
   :name: seq-transaction-error

   seqdiag {
      span_height = 10;
               業務プログラム（画面）; インターセプター; 業務プログラム; エンティティ; JTA;

              業務プログラム（画面） -> インターセプター [label="業務プログラム実行", rightnote="呼び出し対象の@FWTransactionalアノテーションで自動的に呼び出される"];
              インターセプター => JTA [label="トランザクション開始"];
              インターセプター -> 業務プログラム [label="業務プログラム実行"];
              業務プログラム => エンティティ [label="データ更新"];
              業務プログラム -> 業務プログラム [failed, label="例外発生", color=red];
              インターセプター <-- 業務プログラム [label="例外スロー", color=red];
              インターセプター => JTA [label="ロールバック", color=red];
              業務プログラム（画面） <- インターセプター [label="例外スロー", color=red];

              業務プログラム（画面）[color=pink]
              業務プログラム[color=pink]
              エンティティ [shape=circle, color=pink]
              インターセプター [color=palegreen]
      JTA [color=palegreen]
   }




使用方法
------------
トランザクションを開始するメソッドに **@FWTransactionalアノテーション** を付ける。アノテーションの引数には使用するデータソースのJNDI名を指定する。

.. code-block:: java
   :emphasize-lines: 1

   @FWTransactional(dataSourceName = "jdbc/ds_handywedge")
   public int insert (AppData appData) {
      // ・・・
   }

既にトランザクションが開始されている状態で、再度 **@FWTransactionalアノテーション** の付いたメソッドが呼び出された場合でも、
新しいトランザクションは開始されず、既に開始されているトランザクションに属する。

-----------------------
ユーザトランザクション
-----------------------
トランザクションは **Handywedge** で管理するため、アプリケーションによるトランザクション処理（コミット、ロールバック）は原則禁止とする。

-------------------
マルチデータソース
-------------------
トランザクションを単位として、複数のデータソースを切り替えてアクセスすることができる。

使用方法
------------
| トランザクションを開始するメソッドに **@FWTransactionalアノテーション** を付ける。
| アノテーションの引数には使用するデータソースのJNDI名を指定する。

.. code-block:: java
   :emphasize-lines: 1, 6

   @FWTransactional(dataSourceName = "jdbc/ds_handywedge")
   public int insert (AppData appData) {
      // ・・・
   }

   @FWTransactional(dataSourceName = "jdbc/ds_other") // JNDI名を指定する
   public AppData find(String key) {
         　// ・・・
   }
