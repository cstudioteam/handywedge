handywedge-pdf2svg-rest
========================================================

# 概要

  ドキュメント形式の変換するAPIを提供する。
  以下の形式の変換を行う。
  
  |分類       |変換元形式       |変換元形式  |
  |-----------|---------------|------|
  |PDF        |*.pdf          |*.svg |

  【依存ツール】
　　　pdf2svg内部依存します。

  【使用法】
  
  - HTML
    ```html
    <form action="webapi/file/converter/svg" method="post" enctype="multipart/form-data">
      <p>Select a file : <input type="file" name="file" size="45" accept=".pdf" /></p>
      <input type="submit" value="Upload PDF" />
    </form>
    ```
  - Curl
    ```bash
    $ curl -iv -F key1=value1 -F upload=@<pdffilename> <endpoint>/pdfsvg/webapi/file/converter/svg
    ```


## ビルド

#### ローカル開発

- Tomcat起動
  
  * Apache Tomcat Maven Plugin環境配置
  
    [Apache Tomcat Maven Plugin](https://tomcat.apache.org/maven-plugin-2.2/)に従い、TomcatとMaven環境を設定します。

- モジュールビルド

  * Deploy
  ```bash
  $ mvn clean tomcat8:deploy -P locale
  ```
  
  * テスト画面起動
  
    [ファイルアップロード](http://localhost:8080/pdftosvg/fileUpload.html)
 
 
#### 検証、本番環境

- モジュールビルド
  ```bash
  $ mvn clean packge -P [profile]
  ```

- モジュール配置
　
　生成のWarファイルをリリース環境へ持ち込みます。

- Dockerサービス起動
  ```bash
  $ docker-compose 
  $ docker-compose up -d
  ```

  ```bash
  $ docker-compose down
  ```

## リリース


# 制約事項

- ページ指定のSVGファイル変換処理において変換対象のPDFファイルにローマ数字(ⅰ、ⅱ...)とアラビア数字（1、2...）のページ番号が混在した場合、ローマ数字(ⅰ、ⅱ...)のページが変換できなくことがあります。

# 外部参照リンク

- [Apache Tomcat Maven Plugin](https://tomcat.apache.org/maven-plugin-2.2/)
- [pdf2svg ソースコードダウンロードURL](https://github.com/dawbarton/pdf2svg/tags)
