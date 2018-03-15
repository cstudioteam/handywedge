FROM tomcat:8.0.50-jre8

# 言語インストール
RUN apt-get update && \
    apt-get -y install task-japanese locales-all

# 言語設定
ENV LANG=ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# warビルド&デプロイ
WORKDIR /tmp/install
RUN rm -rf /usr/local/tomcat/webapps/* && \
    echo "export LANG=ja_JP.UTF-8" > /usr/local/tomcat/bin/setenv.sh && \
    echo "export JAVA_OPTS=-Duser.timezone=JST" >> /usr/local/tomcat/bin/setenv.sh && \
    chmod +x /usr/local/tomcat/bin/setenv.sh && \
    wget https://github.com/cstudioteam/handywedge/archive/master.zip && \
    unzip master.zip && \
    curl -LO -b "oraclelicense=accept-securebackup-cookie" \
    "http://download.oracle.com/otn-pub/java/jdk/8u162-b12/0da788060d494f5095bf8624735fa2f1/server-jre-8u162-linux-x64.tar.gz" && \
    tar xzf server-jre-8u162-linux-x64.tar.gz && \
    export JAVA_HOME=/tmp/install/jdk1.8.0_162 && \
    wget http://ftp.tsukuba.wide.ad.jp/software/apache/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz && \
    tar xzf apache-maven-3.5.2-bin.tar.gz && \
    cd  handywedge-master/handywedge-binarystore && \
    /tmp/install/apache-maven-3.5.2/bin/mvn package && \
    cp /tmp/install/handywedge-master/handywedge-binarystore/target/handywedge-binarystore.war /usr/local/tomcat/webapps/

# インストーラー等削除
WORKDIR /root
RUN rm -rf /tmp/install/

# 上書き可能環境変数
ENV LOG_LEVEL=info
ENV HW_STORAGE=S3
ENV AWS_ACCESS_KEY_ID=
ENV AWS_SECRET_KEY=

CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
