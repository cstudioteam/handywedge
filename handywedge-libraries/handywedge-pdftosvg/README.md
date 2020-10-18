handywedge-pdf2svg
========================================================

# 概要

  ドキュメント形式の変換するライブラリを提供する。
  以下の形式の変換を行う。
  
  |分類       |変換元形式       |変換元形式  |
  |-----------|---------------|------|
  |PDF        |*.pdf          |*.svg |

  
# pdf2svgコマンド環境構築

### pdf2svg ローカル構築

- macOS
```
$ sudo brew install pdf2svg
```

- linux

  * Debian,Ubuntu
  ```
  $ sudo apt install pdf2svg
  ```
  
  * centos
  ```
  $ sudo yum install pdf2svg
  ```

- windows

  [インストール手順](http://tako.nakano.net/textext_pdf2svg.html)

### pdf2svg Docker環境

- Docker起動
```
 $ docker-compose build --no-cache 
 $ docker-compose up -d
```

- PDF->SVG変換
```
　$ docker-compose exec pdf2svg /bin/bash -c "pdf2svg /tmp/DockerEngine導入ガイド(Linux).pdf /tmp/aaaa_%05d.svg all " 
```

- Docker停止
```
 $ docker-compose down
```

## ビルド

## リリース

# 注意事項


# 外部参照リンク

- [pdf2svg ソースコードダウンロードURL](https://github.com/dawbarton/pdf2svg/tags)
