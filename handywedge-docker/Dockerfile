FROM centos:7

# systemd関連の設定（公式からの手順）
# https://docs.docker.com/samples/library/centos/#systemd-integration
ENV container docker
RUN (cd /lib/systemd/system/sysinit.target.wants/; for i in *; do [ $i == \
systemd-tmpfiles-setup.service ] || rm -f $i; done); \
rm -f /lib/systemd/system/multi-user.target.wants/*;\
rm -f /etc/systemd/system/*.wants/*;\
rm -f /lib/systemd/system/local-fs.target.wants/*; \
rm -f /lib/systemd/system/sockets.target.wants/*udev*; \
rm -f /lib/systemd/system/sockets.target.wants/*initctl*; \
rm -f /lib/systemd/system/basic.target.wants/*;\
rm -f /lib/systemd/system/anaconda.target.wants/*;


# yum update
RUN yum -y update && yum clean all

# ロケール設定
RUN yum reinstall -y glibc-common && yum clean all
RUN yum -y reinstall glibc-common
RUN localedef -v -c -i ja_JP -f UTF-8 ja_JP.UTF-8; echo "";
ENV LANG=ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime
RUN ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# postgresqlインストール
RUN yum -y install postgresql-server postgresql

# postgresql初期化＆設定バックアップ
RUN su - postgres -c "initdb"
RUN cp /var/lib/pgsql/data/postgresql.conf /var/lib/pgsql/data/postgresql.conf.bak
RUN cp /var/lib/pgsql/data/pg_hba.conf /var/lib/pgsql/data/pg_hba.conf.bak

# 設定値置換
RUN sed -i -e "s/#listen_addresses = 'localhost'/listen_addresses = 'localhost'/g" /var/lib/pgsql/data/postgresql.conf

# 自動起動
RUN systemctl enable postgresql

# gitクローン
RUN yum -y install git
RUN cd /tmp && mkdir git && cd /tmp/git &&\
    git clone https://github.com/cstudioteam/handywedge.git

# データベース、DDL、DML
RUN su - postgres -c "pg_ctl start -w && \
    cd /tmp/git/handywedge && \
    psql -f ./handywedge-test-app/sql/create_db.sql && \
    psql -f ./handywedge-test-app/sql/create_user.sql && \
    psql -f ./handywedge-test-app/sql/create_schema.sql && \
    psql -d handywedge_test_app -U handywedge-app -f ./handywedge-test-app/sql/create_table.sql && \
    psql -d handywedge_test_app -U handywedge -f ./handywedge-master/sql/ddl.sql && \
    psql -d handywedge_test_app -U handywedge -f ./handywedge-test-app/sql/dml.sql && \
    pg_ctl stop -m fast \
"

# systemd起動。常に最後に記載する
VOLUME [ "/sys/fs/cgroup" ]
CMD ["/usr/sbin/init"]
