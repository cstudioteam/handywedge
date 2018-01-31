メール機能
============
.. index::
   single: FWMailMessage
   single: SMTP
   pair: FWMailTransport; インターフェイス

------------
メール送信
------------
メールを送信する場合は、**Handywedge** が提供するメール送信機能を使う。
メール送信機能を使用するには、**Handywedge** プロパティにSMTPに関する設定が必要となる。

使用方法
------------
**FWMailTransportインターフェイス** の変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。
**FWMailMessage** のインスタンスを生成し、必要な項目を設定して **FWMailTransport** オブジェクトで送信する。

.. seqdiag::
   :name: seq-report

   seqdiag {
      span_height = 10;
              業務プログラム; FWMailMessage; FWMailTransport;

              業務プログラム => FWMailMessage [label="setFromAddress(fromAddr)", rightnote="送信元アドレスをセット"];
              業務プログラム => FWMailMessage [label="setToAddress(toAddrs)", rightnote="宛先（To）アドレスをセット"];
              業務プログラム => FWMailMessage [label="setCcAddress(ccAddrs)", rightnote="宛先（Cc）アドレスをセット"];
              業務プログラム => FWMailMessage [label="setSubject(toAddr)", rightnote="件名をセット"];
              業務プログラム => FWMailMessage [label="setCharacterEncoding(FWMailCharacterEncoding)", rightnote="文字コードをセット"];
              業務プログラム => FWMailMessage [label="setPriority(FWMaiPriority)", rightnote="優先度をセット"];
              業務プログラム => FWMailMessage [label="setHtmlFlg(true)", rightnote="送信形式をセット"];
              業務プログラム => FWMailMessage [label="setBody(text)", rightnote="本文をセット"];
              業務プログラム => FWMailTransport [label="send(FWMailMessage)", rightnote="メール送信依頼"];

             業務プログラム [color=pink]
      FWMailMessage [color=palegreen]
      FWMailTransport [color=palegreen]
   }

サンプルコード
---------------

.. code-block:: java
   :emphasize-lines: 2, 5, 15

    @Inject
    private FWMailTransport mail;

    // FWMailMessageのインスタンスを生成して値をセットし、FWMailTransportで送信する。
    FWMailMessage mailMessage = new FWMailMessage();
    mailMessage.setToAddress(new String[] { "aaa@bbb.co.jp" }); // TO
    mailMessage.setFromAddress("ccc@ddd.co.jp"); // FROM
    mailMessage.setCcAddress(new String[] { "eee@fff.co.jp" }); // CC
    mailMessage.setSubject("Is this Test?"); // 件名
    mailMessage.setCharacterEncoding(FWMailCharacterEncoding.ISO_2022_JP); // 文字コード。デフォルトでUTF-8に設定
    mailMessage.setPriority(FWMaiPriority.HIGH); // 重要度。デフォルトはNormalで設定済。
    mailMessage.setHtmlFlg(true); // HTML形式で送信。 デフォルトはTextで送信
    mailMessage.setBody("This is test mail."); // 本文
    try {
        mail.send(mailMessage);
    } catch (FWException e) {
        logger.error("mail api error.", e);
    }


Handywedgeプロパティ
------------------------------
**Handywedge** プロパティのメール送信項目を適切に設定する。

::

   # SMTPサーバー #必須
   fw.mail.host=smtp.mycompany.com

   # SMTPポート #def=25
   fw.mail.port=587

   # 添付ファイル上限値（合計） #def=0=無制限
   fw.mail.max_filesize=0

   # STARTTLS拡張方式を有効化 #def=false
   fw.mail.starttls.enable=true

   # STARTTLSが必須か #def=false
   fw.mail.starttls.required=true

   # SMTPユーザー #必須
   fw.mail.user=xxxxx@cstudio.jp

   # SMTPパスワード #必須
   fw.mail.password=password


