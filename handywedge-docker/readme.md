# マイクロサービスビルド
※1つ上のリポジトリのルートディレクトリで実行する
## pdf2svg
```
docker build --rm -t handywedge/pdf2svg:master -f ./handywedge-docker/pdf2svg/Dockerfile ./
docker save handywedge/pdf2svg:master | gzip > handywedge-pdf2svg.tgz
```

## バイナリストア
```
docker build --rm -t handywedge/binarystore/aws:master -f handywedge-docker/binarystore/aws/Dockerfile ./
docker save handywedge/binarystore/aws:master | gzip > handywedge-binarystore-aws.tgz
```


# dockerエンジンのインストール
##### インストール環境：CentOS7

[参考](https://docs.docker.com/engine/installation/linux/docker-ce/centos/)

```
sudo yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2

sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo

sudo yum makecache fast

sudo yum install docker-ce

sudo systemctl enable docker

sudo systemctl start docker
```

# docker build,run,execの手順
```
sudo docker build --rm -t [タグ(任意)] [Dockerfileのパス]

sudo docker run --privileged -d -i -v /sys/fs/cgroup:/sys/fs/cgroup:ro --name [コンテナ名(任意)] [タグ]

sudo docker exec -it [コンテナ名] /bin/bash
```

# export,import,runの手順
```
sudo docker export [コンテナ名] | gzip > [任意のファイル名].tgz

sudo docker import -c "CMD /usr/sbin/init" -c "ENV LANG=ja_JP.UTF-8" https://s3-ap-northeast-1.amazonaws.com/handywedge/docker/images/handywedge.tgz [リポジトリ名:タグ名（オプション）]

sudo docker run --privileged -d -i -p 8080:8080 -v /sys/fs/cgroup:/sys/fs/cgroup:ro --name [コンテナ名(任意)] [タグ]
```

# ネットワーク作成とIP固定
```
sudo docker network create --subnet 172.20.0.0/16 handywedge-network
sudo docker run --net handywedge-network --ip 172.20.0.2 ・・・
```
