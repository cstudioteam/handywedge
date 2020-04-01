Handywedge カレンダーサービス
------------------------------------

## 事前準備

- JDKをインストール
    
    [Download Amazon Corretto 8](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/patches.html) 
    
- Mavenをインストール

    [Download Apache Maven 3.3+](http://maven.apache.org/download.cgi)
    
    [Installing Apache Maven](https://maven.apache.org/install.html)


## ビルド

- ローカル
    ```
    $ mvn clean package [-P local]
    ```
- 開発
    ```
    $ mvn clean package -P dev [--offline package -Dmaven.repo.local=repository -Dmaven.legacyLocalRepo=true]
    ```
    ※オフラインでビルドを行う際は、[]の部分まで指定してください。
    
    ローカルリポジトリ作成については[ローカルリポジトリ作成](ローカルリポジトリ作成)を参照してください。
    

- 検証（ステージング）
    ```
    $ mvn clean package -P stg [--offline package -Dmaven.repo.local=repository -Dmaven.legacyLocalRepo=true]
    ```
    ※オフラインでビルドを行う際は、[]の部分まで指定してください。
    
    ローカルリポジトリ作成については[ローカルリポジトリ作成](ローカルリポジトリ作成)を参照してください。
 
- 本番
    ```
    $ mvn clean package -P prd [--offline package -Dmaven.repo.local=repository -Dmaven.legacyLocalRepo=true]
    ```
    ※オフラインでビルドを行う際は、[]の部分まで指定してください。
    
    ローカルリポジトリ作成については[ローカルリポジトリ作成](ローカルリポジトリ作成)を参照してください。
 

## リリース

　上記「ビルド」手順を完了すると、[build/libs/hw-cal.war]が作成される。
　warファイル、リリース対象環境へ配置する。

# ローカルリポジトリ作成
　CI/CDのビルドにおいて、Publicリポジトリへのアクセスができないため、ローカル環境から必要な
　すべてのライブラリを事前用意しておく必要があります。

    ```
    $ cd [プロジェクトルート]
    $ rm -rf repository/*
    $ mvn dependency:go-offline -Dmaven.repo.local=repository clean package -P dev
    ```

