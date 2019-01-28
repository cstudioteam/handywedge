FROM centos:7

# systemd関連の設定
# https://docs.docker.com/samples/library/centos/#systemd-integration
ENV container docker
RUN (cd /lib/systemd/system/sysinit.target.wants/; for i in *; do [ $i == \
    systemd-tmpfiles-setup.service ] || rm -f $i; done); \
    rm -f /lib/systemd/system/multi-user.target.wants/*; \
    rm -f /etc/systemd/system/*.wants/*; \
    rm -f /lib/systemd/system/local-fs.target.wants/*; \
    rm -f /lib/systemd/system/sockets.target.wants/*udev*; \
    rm -f /lib/systemd/system/sockets.target.wants/*initctl*; \
    rm -f /lib/systemd/system/basic.target.wants/*; \
    rm -f /lib/systemd/system/anaconda.target.wants/*;

# yum updateと必要なパッケージをインストール
# ロケール関連：glibc-common
# postgres関連：postgresql,postgresql-server
# アプリケーション関連：git,which,jdk
RUN yum -y update && \
    yum clean all && \
    yum -y reinstall glibc-common && \
    yum -y install \
    git \
    postgresql \
    postgresql-server \
    which \
    java-1.8.0-openjdk-devel

# ロケール設定
RUN localedef -v -c -i ja_JP -f UTF-8 ja_JP.UTF-8; echo "";
ENV LANG=ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# postgresql初期化＆設定バックアップ&設定
RUN su - postgres -c "initdb" && \
    cp /var/lib/pgsql/data/postgresql.conf /var/lib/pgsql/data/postgresql.conf.bak && \
    cp /var/lib/pgsql/data/pg_hba.conf /var/lib/pgsql/data/pg_hba.conf.bak && \
    sed -i -e "s/#listen_addresses = 'localhost'/listen_addresses = 'localhost'/g" /var/lib/pgsql/data/postgresql.conf && \
    systemctl enable postgresql

# gitクローン
RUN cd /tmp && \
    mkdir git && \
    cd /tmp/git && \
    git clone https://github.com/cstudioteam/handywedge.git

# データベース、DDL、DML
RUN su - postgres -c "pg_ctl start -w && \
    cd /tmp/git/handywedge && \
    psql -f ./handywedge-test-app/sql/create_db.sql && \
    psql -f ./handywedge-test-app/sql/create_user.sql && \
    psql -f ./handywedge-test-app/sql/create_schema.sql && \
    psql -d handywedge-db -U handywedge-app -f ./handywedge-test-app/sql/create_table.sql && \
    psql -d handywedge-db -U handywedge -f ./handywedge-master/sql/ddl.sql && \
    psql -d handywedge-db -U handywedge -f ./handywedge-test-app/sql/dml.sql && \
    pg_ctl stop -m fast \
"

# tomcat
WORKDIR /opt/
RUN curl -O https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.14/bin/apache-tomcat-9.0.14.tar.gz && \
    tar xzf apache-tomcat-9.0.14.tar.gz && \
    ln -s apache-tomcat-9.0.14 tomcat && \
    rm -rf tomcat/webapps/* && \
    mkdir -p tomcat/conf/Catalina/localhost && \
    echo "export LANG=ja_JP.UTF-8" > tomcat/bin/setenv.sh && \
    chmod +x tomcat/bin/setenv.sh && \
    useradd -r -s /sbin/nologin tomcat && \
    chown -R tomcat:tomcat apache-tomcat-9.0.14 && \
    rm apache-tomcat-9.0.14.tar.gz && \
    echo "[Unit]" > /etc/systemd/system/tomcat.service && \
    cd /etc/systemd/system && \
    echo "Description=Apache Tomcat 9" >> tomcat.service && \
    echo "After=network.target postgresql.service" >> tomcat.service && \
    echo "" >> tomcat.service && \
    echo "[Service]" >> tomcat.service && \
    echo "User=tomcat" >> tomcat.service && \
    echo "Group=tomcat" >> tomcat.service && \
    echo "Type=oneshot" >> tomcat.service && \
    echo "PIDFile=/opt/tomcat/tomcat.pid" >> tomcat.service && \
    echo "RemainAfterExit=yes" >> tomcat.service && \
    echo "" >> tomcat.service && \
    echo "ExecStart=/opt/tomcat/bin/startup.sh" >> tomcat.service && \
    echo "ExecStop=/opt/tomcat/bin/shutdown.sh" >> tomcat.service && \
    echo "ExecReload=/opt/tomcat/bin/shutdown.sh;/opt/tomcat/bin/startup.sh" >> tomcat.service && \
    echo "" >> tomcat.service && \
    echo "[Install]" >> tomcat.service && \
    echo "WantedBy=multi-user.target" >> tomcat.service && \
    systemctl enable tomcat

# warビルド&デプロイ
WORKDIR /tmp/install
RUN curl -O https://archive.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.tar.gz && \
    tar xzf apache-maven-3.5.4-bin.tar.gz && \
    cd /tmp/git/handywedge/handywedge-dependencies/ && \
    /tmp/install/apache-maven-3.5.4/bin/mvn install && \
    cd /tmp/git/handywedge/handywedge-master/ && \
    /tmp/install/apache-maven-3.5.4/bin/mvn -Dmaven.javadoc.skip=true install && \
    cd /tmp/git/handywedge/handywedge-test-app/ && \
    /tmp/install/apache-maven-3.5.4/bin/mvn package && \
    cp /tmp/git/handywedge/handywedge-test-app/target/handywedge-test-app.war /opt/tomcat/webapps/ && \
    chown tomcat:tomcat /opt/tomcat/webapps/handywedge-test-app.war

# インストーラー等削除
WORKDIR /root
RUN rm -rf /tmp/git/ && \
    rm -rf .m2 && \
    rm -rf /tmp/install/

# systemd起動。常に最後に記載する
VOLUME [ "/sys/fs/cgroup" ]
CMD ["/usr/sbin/init"]
