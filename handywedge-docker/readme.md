# dockerエンジンのインストール
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
