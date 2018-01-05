# dockerエンジンのインストール

```
yum -y install docker
systemctl enable docker.service
systemctl start docker.service
```

# docker build,run,execの手順

- dockerfileのある階層に移動、もしくはパスをかく
- posgretestの部分は任意

```
sudo docker build --rm -t posgretest .
sudo docker run --privileged -d -i -v /sys/fs/cgroup:/sys/fs/cgroup:ro --name posgretest posgretest
sudo docker exec -it posgretest /bin/bash
```

