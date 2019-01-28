FROM tomcat:8.0.50-jre8

# 言語インストール
RUN apt-get update && \
    apt-get -y install task-japanese locales-all openjdk-8-jdk

# 言語設定
ENV LANG=ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# warビルド&デプロイ
WORKDIR /tmp/install
RUN rm -rf $CATALINA_HOME/webapps/* && \
    echo "export LANG=ja_JP.UTF-8" > /usr/local/tomcat/bin/setenv.sh && \
    echo "export JAVA_OPTS=-Duser.timezone=JST" >> /usr/local/tomcat/bin/setenv.sh && \
    chmod +x /usr/local/tomcat/bin/setenv.sh && \
    wget https://github.com/cstudioteam/handywedge/archive/master.zip && \
    unzip master.zip && \
    wget https://archive.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.tar.gz && \
    tar xzf apache-maven-3.5.4-bin.tar.gz && \
    cd  handywedge-master/handywedge-binarystore && \
    /tmp/install/apache-maven-3.5.4/bin/mvn package && \
    cp /tmp/install/handywedge-master/handywedge-binarystore/target/handywedge-binarystore.war $CATALINA_HOME/webapps/

# インストーラー等削除
WORKDIR /root
RUN rm -rf /tmp/install/ && \
    rm -rf .m2 && \
    apt -y purge openjdk-8-jdk && \
    apt -y autoremove

# 上書き可能環境変数
ENV LOG_LEVEL=info
ENV HW_STORAGE=S3
ENV AWS_ACCESS_KEY_ID=
ENV AWS_SECRET_KEY=

CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
