# アプリケーション開発ガイドのビルド
## 前提
Dockerが動作する環境

## 手順
1. Dockerfileのあるパスまで移動します。 `cd /path/to/developper-guide`
1. dockerコマンドを実行します。 `docker run --rm -v $PWD:/docs $(docker build -q .) make clean html`
1. `_build` ディレクトリに生成されます。
