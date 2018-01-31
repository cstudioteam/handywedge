アプリケーションの配置
===========================

アプリケーションのモジュールは :numref:`arrangement-war-image` に示すようなwarファイルの形式で構成する。

.. code-block:: text
   :name: arrangement-war-image
   :caption: warファイルの構成例

   handywedge-sample.war
   ├── META-INF
   │ └── context.xml　tomcat設定ファイル
   ├── WEB-INF
   │ ├── beans.xml　CDI設定
   │ ├── faces-config.xml　JSF設定
   │ ├── web.xml　Webアプリケーション設定
   │ ├── classes
   │ │ ├── handywedge-sample_en.properties　システムプロパティ（ロケール米国）
   │ │ ├── handywedge-sample_ja.properties　システムプロパティ（ロケール日本）
   │ │ ├── handywedge-sample.properties　システムプロパティ
   │ │ ├── jasperreport_extension.properties　帳票設定
   │ │ ├── log4j2.xml　ロギング設定
   │ │ ├── report
   │ │ │ └── *.jasper　帳票テンプレート
   │ │ └── jp
   │ │    └── cstudio
   │ │      └── handywedge
   │ │        └── sample
   │ │          ├── *.class　Javaクラス
   │ │          └...
   │ └── lib
   │   ├── handywedge-api-*.jar　FWライブラリ
   │   ├── handywedgw-core-*.jar　FWライブラリ
   │   └── *.jar　FWおよびAPの依存ライブラリ
   ├── index.xhtml　Webリソース
   …

