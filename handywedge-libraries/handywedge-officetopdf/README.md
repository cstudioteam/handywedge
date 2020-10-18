handywedge-office2pdf
========================================================

# 概要

  ドキュメント形式の変換するライブラリを提供する。
  以下の形式の変換を行う。
  
  |分類       |変換元形式       |変換元形式  |
  |-----------|---------------|------|
  |MS Office  |*.xls, *.xlxs  |*.pdf |
  |MS Office  |*.doc, *.docx  |*.pdf |
  |MS Office  |*.ppt, *.pptx  |*.pdf |
  
# 環境構築

## インストール

## ビルド

## リリース

## サービス起動
```

```
# 注意事項

## パフォーマンス改善

- javaバージョン

  - java 1.8.191以降、java 9.0.4以降バージョンを推薦

- LocalOfficeManagerよりExternalOfficeManagerを利用

　 ExternalOfficeManagerを利用する際、プロンプトよりLibreOfficeを起動しておく必要がある。
   ```
   $ soffice -accept="socket,host=127.0.0.1,port=2002;urp;"
   ```

# 外部参照リンク

- [LibreOffice](https://ja.libreoffice.org/)

  [LibreOffice Online](https://groupoffice.readthedocs.io/en/latest/install/extras/libreoffice-online.html)

- [JodConverter](https://github.com/sbraconnier/jodconverter/wiki)

- [libreoffice/online docker](https://hub.docker.com/r/libreoffice/online)
