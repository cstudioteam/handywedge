REST機能
=========
.. index::
   single: REST
   pair: FWRESTController; アノテーション
   pair: FWRESTRequest; アノテーション
   pair: FWRESTResponse; アノテーション

.. _rest-receive:

---------
REST実行
---------
REST実行機能では、**Handywedge** が提供するクラスを継承してビジネスロジックを実装することで、RESTでリクエストを受け付けてビジネスロジックを実行することができる。
実行するクラスはRESTのエンドポイントで決定する。

リクエストとレスポンスはともに **Handywedge** が提供するクラスを継承しJSONに変換できる形式で実装する。

URLに指定する実行クラス名はパッケージ付きで指定する。

::

   http://APサーバー/コンテキストパス/fw/rest/app/パッケージ．実行クラス名


使用方法
-----------
ビジネスロジック、リクエスト、レスポンスを **Handywedge** が提供するクラスを継承する形で実装する。

.. seqdiag::
   :name: seq-report

   seqdiag {
      span_height = 20;
      RESTクライアント; FWRESTFilter; RESTビジネスクラス; リクエストクラス; レスポンスクラス;

      RESTクライアント -> FWRESTFilter [label="GET/POST", rightnote="HTTPリクエスト"];
      FWRESTFilter => リクエストクラス [label="set(value)", rightnote="JSONからJavaオブジェクトに変換"];
      FWRESTFilter -> RESTビジネスクラス [label="doGet(Request)", rightnote="POSTの場合はdoPost（）"];
      RESTビジネスクラス => リクエストクラス [label="getXxxx()"];
      RESTビジネスクラス => レスポンスクラス [label="setXxxx(value)", rightnote="戻り値をセット"];
      FWRESTFilter <-- RESTビジネスクラス [label="レスポンスクラス"];
      FWRESTFilter => レスポンスクラス [label="getXxxx()", rightnote="JavaオブジェクトからJSONに変換"];
      RESTクライアント <-- FWRESTFilter [label="JSON"];

      RESTクライアント [shape=actor]
      RESTビジネスクラス [color=pink]
      リクエストクラス [color=pink]
      レスポンスクラス [color=pink]
      FWRESTFilter [color=palegreen]
   }


ビジネスロジッククラス
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
ビジネスロジッククラスは **FWRESTController** を継承する。メソッドの **@FWRESTRequestClassアノテーション** で対応するリクエストクラスを指定する。

基本的にアプリケーション例外はスローしないように実装し、エラー応答はエラーコードで表現する。
例外がスローされた場合は **Handywedge** でキャッチし、エラーコード-9000台を設定してクライアントに応答する。

.. code-block:: java
   :emphasize-lines: 1, 10, 37-41

   public class SampleBatch extends FWRESTController {

      @Inject
      private Logger logger;

      @Inject
      private SampleBatchService service;

      @Override
      @FWRESTRequestClass(SampleBatchRequest.class)
      public FWRESTResponse doPost(FWRESTRequest request) {
           try {
               logger.info("サンプルバッチを実行します。");
               SampleBatchRequest parameter = (SampleBatchRequest) request;
               logger.debug("parameter=" + parameter);

               List<Data> targetList;
               logger.info("対象データを取得します。");
               if (parameter.getFrom() != null && parameter.getTo() != null) {
                  targetList = service.selectBetweenDate(parameter.getFrom(), parameter.getTo());
               } else {
                   targetList = service.selectAll();
               }
               logger.debug("target count=" + targetList.size());
               // ・・・
               SampleBatchResponse response = new SampleBatchResponse();
               response.setReturn_cd(0);
               response.setReturn_msg("success");
               SampleBatchResponseParameter para = new SampleBatchResponseParameter();
               para.setCount(count);
               response.setParameter(para);
               logger.info("対象データの処理が終了しました。件数=" + count);

               logger.info("サンプルバッチを終了します。");
               return response;
            } catch (Exception e) {
               logger.error("バッチ実行中にエラーが発生しました。", e);
               SampleBatchResponse response = new SampleBatchResponse();
               response.setReturn_cd(-1);
               response.setReturn_msg(e.getMessage());
               return response;
         }
      }
   }


リクエストクラス
~~~~~~~~~~~~~~~~~~~~~
リクエストクラスは **FWRESTRequest** を継承する。
必要があれば `JacksonライブラリのAPI <https://github.com/FasterXML/jackson>`_ を参照してアノテーションで装飾する。

.. code-block:: java

   public class SampleBatchRequest extends FWRESTRequest {

      private String from;
      private String to;

      public String getFrom() {
           return from;
      }

      public void setFrom(String from) {
         this.from = from;
      }

      public String getTo() {
           return to;
      }

      public void setTo(String to) {
           this.to = to;
      }

      @Override
      public String toString() {
          StringBuilder builder = new StringBuilder();
          builder.append("SampleBatchRequest [from=").append(from).append(", to=").append(to).append("]");
          return builder.toString();
      }

   }


レスポンスクラス
~~~~~~~~~~~~~~~~~~~~~~~~~~
レスポンスクラスは **FWRESTResponse** を継承する。
リターンコードとリターンメッセージの2つのプロパティは **Handywedge** で規定し、リターンコードは必ず設定する。
負の値はエラーを示し、-9000～-9999については **Handywedge** の予約番号とする。

.. code-block:: java

   public abstract class CommonResponse<P> extends FWRESTResponse {

       @JsonProperty("PARA")
      public abstract P getParameter();

      @JsonProperty("PARA")
      public abstract void setParameter(P parameter);

   }

   public class SampleBatchResponse extends CommonResponse<SampleBatchResponseParameter> {

       private SampleBatchResponseParameter para;

      @Override
       public SampleBatchResponseParameter getParameter() {
           return para;
      }

      @Override
      public void setParameter(SampleBatchResponseParameter parameter) {
           this.para = parameter;
      }

      @Override
      public String toString() {
           StringBuilder builder = new StringBuilder();
          builder.append("SampleBatchResponse [para=").append(para).append(", getReturn_cd()=").append(getReturn_cd())
          .append(", getReturn_msg()=").append(getReturn_msg()).append("]");
         return builder.toString();
      }
   }

   public class SampleBatchResponseParameter {

       private int count;

      public int getCount() {
           return count;
      }

      public void setCount(int count) {
           this.count = count;
      }

      @Override
      public String toString() {
           StringBuilder builder = new StringBuilder();
          builder.append("SampleBatchResponseParameter [count=").append(count).append("]");
          return builder.toString();
       }
   }


上記のようなレスポンスクラスを実装した場合、JSONは次のようになる。

.. code-block:: json

   {"RETURN_CD":0,"RETURN_MSG":"success","PARA":{"count":1752}}


RESTフィルター
----------------
web.xmlに **Handywedge** が提供するREST用フィルターの設定を次のように行う。

.. code-block:: xml

   <filter-mapping>
      <filter-name>handywedge_rest_filter</filter-name>
      <url-pattern>/fw/rest/*</url-pattern>
   </filter-mapping>



.. rest-prefix:

プレフィックス登録
-----------------------
予め **Handywedge** プロパティにプレフィックスを登録しておくことでURLで指定する実行クラス名を短縮することができる。
プレフィックスの登録は **Handywedge** プロパティのキー名をfw.rest.に続けて短縮名を、バリュー値にパッケージを含む実行クラス名を指定する。

例）　jp.cstudio.handywedge.test.app.rest.RESTKeyValueStoreクラスを「kvs」としてプレフィックス（短縮名）で定義する場合。

::

   # RESTプレフィックスパスマッピング
   fw.rest.kvs=jp.cstudio.handywedge.test.app.rest.RESTKeyValueStore


