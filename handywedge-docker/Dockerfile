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

#JDK
RUN cd /tmp && mkdir install && cd install && \
    curl -LO -b "oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u152-b16/aa0333dd3019491ca4f6ddbe78cdb6d0/server-jre-8u152-linux-x64.tar.gz" && \
    tar xzf server-jre-8u152-linux-x64.tar.gz
RUN mkdir /usr/java && \
    mv /tmp/install/jdk1.8.0_152 /usr/java/
RUN cd  /usr/bin && \
    ln -s /usr/java/jdk1.8.0_152/bin/java java
ADD java.sh /etc/profile.d/java.sh
ADD java.csh /etc/profile.d/java.csh
RUN exec -l $SHELL

#tomcat
RUN yum -y install wget
WORKDIR /opt/
RUN wget https://archive.apache.org/dist/tomcat/tomcat-8/v8.0.47/bin/apache-tomcat-8.0.47.tar.gz && \
    tar xzf apache-tomcat-8.0.47.tar.gz && \
    ln -s apache-tomcat-8.0.47 tomcat && \
    rm -rf tomcat/webapps/* && \
    useradd -r -s /sbin/nologin tomcat && \
    chown -R tomcat:tomcat apache-tomcat-8.0.47 && \
    rm apache-tomcat-8.0.47.tar.gz
ADD tomcat.service /etc/systemd/system/tomcat.service
RUN chmod 755 /etc/systemd/system/tomcat.service
RUN systemctl enable tomcat


# warビルド&デプロイ
WORKDIR /tmp/install
RUN yum install -y which
RUN wget http://ftp.tsukuba.wide.ad.jp/software/apache/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz && \
    tar xzf apache-maven-3.5.2-bin.tar.gz && \
    wget https://sourceforge.net/p/itext/code/HEAD/tree/tags/iText_2_1_7/lib/iTextAsian.jar?format=raw -O iTextAsian.jar
RUN /tmp/install/apache-maven-3.5.2/bin/mvn install:install-file -Dfile=iTextAsian.jar -DgroupId=com.lowagie -DartifactId=iTextAsian -Dversion=2.1.7 -Dpackaging=jar
RUN cd /tmp/git/handywedge/handywedge-dependencies/ && /tmp/install/apache-maven-3.5.2/bin/mvn install
RUN cd /tmp/git/handywedge/handywedge-master/ && \
    /tmp/install/apache-maven-3.5.2/bin/mvn -Dmaven.javadoc.skip=true install
RUN cd /tmp/git/handywedge/handywedge-test-app/ && \
    /tmp/install/apache-maven-3.5.2/bin/mvn install
#RUN chown tomcat:tomcat /tmp/git/handywedge/handywedge-test-app/target/handywedge-test-app.war
RUN cp /tmp/git/handywedge/handywedge-test-app/target/handywedge-test-app.war /opt/tomcat/webapps/
RUN chown tomcat:tomcat /opt/tomcat/webapps/handywedge-test-app.war



# インストーラー等削除
RUN rm -rf /tmp/git/ && \
    rm -rf /tmp/install/


# systemd起動。常に最後に記載する
VOLUME [ "/sys/fs/cgroup" ]
CMD ["/usr/sbin/init"]
