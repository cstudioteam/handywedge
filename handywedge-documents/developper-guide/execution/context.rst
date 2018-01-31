コンテキスト管理
=================
.. index::
   single: CDI（Contexts and Dependency Inject）
   single:　beans.xml
   single: Managed Bean
   pair: @Named; アノテーション
   pair: @Produces; アノテーション
   pair: fwContext; 暗黙オブジェクト

CDI（Contexts and Dependency Inject）を活用すると、依存するオブジェクトを注入するだけでなく次のようなメリットを享受できる。

+ インスタンスのライフサイクル管理やオブジェクトの生成、値のセットなどが簡単になる
+ コンポーネントやクラスを疎結合にできる

.. important:: CDIを利用するには、warファイルのWEB-INF配下にbeans.xmlファイルを配置する。


----------------------
オブジェクトの注入
----------------------
CDIでオブジェクトを注入するには、変数の宣言に@Injectアノテーションを付ける。

.. code-block:: java
   :emphasize-lines: 1

    @Inject
    private FWLoginManager loginMgr;



----------------------
動的オブジェクトの注入
----------------------
条件によって注入するオブジェクトを変えたい場合には、@Producesアノテーションを付けたメソッドを定義し、メソッドの戻り値としてオブジェクトを返すことで動的に注入するオブジェクトを変更することができる。

.. code-block:: java
   :emphasize-lines: 1

    @Produces
    public Certain getCertain() {
        ;
        return new Certain();
    }


------------------
EL式からのアクセス
------------------
JSFではManaged Beanに@Namedアノテーションを付けることで、JSFのEL式からアクセスすることができる。

また、暗黙オブジェクト **[fwContext]** を用い、アプリケーション情報、ユーザー情報（ログインユーザー）を取得することができる。

使用方法
--------

.. code-block:: html
   :emphasize-lines: 1

   <span>あなたのロールは<h:outputText value="#{fwContext.user.roleName}" />です</span>
