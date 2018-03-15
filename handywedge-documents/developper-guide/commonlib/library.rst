ライブラリ機能
================
フレームワークでは、利用頻度が高いと思われるユーティリティをライブラリとして提供している。
各クラスの具体的な使用方法はJava Docを参照。

------------------------
パスワードユーティリティ
------------------------
フレームワークはパスワードを暗号化するための機能を提供する。

クラス名
---------

::

    com.handywedge.common.FWPasswordUtil



メソッドサマリー
~~~~~~~~~~~~~~~~~

.. list-table::
    :header-rows: 1

    * - | 修飾子と型
      - | メソッドと説明
    * - | public static String
      - | **createPasswordHash** (String password)
        | passwordのハッシュ値を取得する。
    * - | public static boolean
      - | **checkPassword** (String password, String ｈashedStr)
        | passwordのハッシュ値とｈashedStrとを比較する。


------------------------
文字列操作ユーティリティ
------------------------
フレームワークは文字列操作ユーティリティ機能を提供する。

.. attention:: 文字コードはUTF-8のみ対応。

クラス名
---------

::

    com.handywedge.common.FWStringUtil


メソッドサマリー
~~~~~~~~~~~~~~~~~

.. list-table::
    :header-rows: 1

    * - | 修飾子と型
      - | メソッドと説明
    * - | public static boolean
      - | **isEmpty** (String src)
        | 文字列がnull（もしくは空文字）かを判定する。
    * - | public static boolean
      - | **isByteMoreThan** (String src, int maxLength)
        | 文字列が指定バイト数より大きいかを判定する。
    * - | public static boolean
      - | **isLengthMoreThan** (String src, int maxLength)
        | 文字列が指定文字数より大きいかを判定する。
    * - | public static String
      - | **getLoginUrl** ()
        | FWプロパティ定義のログイン画面URL（fw.login.url）を返す。
    * - | public static String
      - | **getRegisterUrl** ()
        | FWプロパティ定義の登録画面URL（fw.register.urlを返す。
    * - | public static String
      - | **getPreRegisterUrl** ()
        | FWプロパティ定義の仮登録画面URL（fw.pre_register.url）を返す。
    * - | public static String
      - | **getActRegisterSuccessUrl** ()
        | FWプロパティ定義の本登録成功時のリダイレクト先（fw.pre.user.register.success.redirect）を返す。
    * - | public static String
      - | **getActRegisterFailUrl** ()
        | FWプロパティ定義の本登録失敗時のリダイレクト先（fw.pre.user.register.fail.redirect）を返す。
    * - | public static String
      - | **getResetPasswdSuccessUrl** ()
        | FWプロパティ定義のパスワード初期化成功時のリダイレクト先（fw.user.register.passwd.reset.success.redirect）を返す。
    * - | public static String
      - | **getResetPasswdFailUrl** ()
        | FWプロパティ定義のパスワード初期化失敗時のリダイレクト先（fw.user.register.passwd.reset.fail.redirect）を返す。
    * - | public static String
      - | **concatContext** (String baseUrl)
        | コンテキストパスにベースURLを加えた文字列を返す。
    * - | public static String
      - | **getIncludeContextUrl** ()
        | コンテキストパスまでのプロトコル付URLを返す。
    * - | public static String
      - | **splitBearerToken** (String tokenHeader)
        | Bearerヘッダー文字列よりトークンを切りだし返す。
    * - | public static String
      - | **replaceNullString** (String src, String replace)
        | 対象文字列がnull（もしくは空文字）の場合、置換文字列に置き換え返す。
    * - | public static String
      - | **trimControlCharset** (String src)
        | 文字列より制御文字（0000-001f、007f）を除去し返す。


--------------------------
暗号・復号ユーティリティ
--------------------------
フレームワークはデータの暗号化・復号機能を提供する。

クラス名
---------

::

    com.handywedge.crypy.FWCipher


使用方法
--------
FWCipherインターフェイスの変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。

.. code-block:: java
   :emphasize-lines: 2

   @Inject
   private FWCipher cipher;

   // 暗号化例（FWプロパティ設定値の鍵データを使用）
   byte[] encData = cipher.encrypt(src.getBytes(StandardCharsets.UTF_8));

   data = Base64.getEncoder().encodeToString(encData);  //暗号化した文字列を取得する例
   // 復号例（FWプロパティ設定値の鍵データを使用）
   byte[] encData = Base64.getDecoder().decode(src);

   data = new String(cipher.decrypt(encData), StandardCharsets.UTF_8);  //復号した文字列を取得する例


メソッドサマリー
~~~~~~~~~~~~~~~~

.. list-table::
    :header-rows: 1

    * - | 修飾子と型
      - | メソッドと説明
    * - | public byte[]
      - | **encrypt** (byte[] rawData)
        | FWプロパティに設定された鍵データ（fw.crypto.key）を使用してrawDataを暗号化する。
    * - | public byte[]
      - | **encrypt** (String key, byte[] rawData)
        | 引数で指定された鍵データ（128bit長）を使用してrawDataを暗号化する。
    * - | public byte[]
      - | **decrypt** (byte[] encData)
        | FWプロパティに設定された鍵データ（fw.cypto.key）を使用してencDataを複合する。
    * - | public byte[]
      - | **decrypt** (String key, byte[] encData)
        | 引数で指定された鍵データ（128bit長）を使用してencDataを複合する。
    * - | public String
      - | **getKey** ()
        | FWプロパティに設定された鍵（fw.crypto.key）を返す。
