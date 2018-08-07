FROM tomcat:8.0.53-jre8

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
RUN rm -rf $CATALINA_HOME/webapps/* && \
    echo "export LANG=ja_JP.UTF-8" > $CATALINA_HOME/bin/setenv.sh && \
    echo "export JAVA_OPTS=-Duser.timezone=JST" >> $CATALINA_HOME/bin/setenv.sh && \
    chmod +x $CATALINA_HOME/bin/setenv.sh && \
    curl -LO https://github.com/cstudioteam/handywedge/archive/master.zip && \
    unzip master.zip && \
    curl -LO -b "oraclelicense=accept-securebackup-cookie" \
    "http://download.oracle.com/otn-pub/java/jdk/8u181-b13/96a7b8442fe848ef90c96a2fad6ed6d1/server-jre-8u181-linux-x64.tar.gz" && \
    tar xzf server-jre-8u181-linux-x64.tar.gz && \
    export JAVA_HOME=/tmp/install/jdk1.8.0_181 && \
    curl -O https://archive.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.tar.gz && \
    tar xzf apache-maven-3.5.4-bin.tar.gz && \
    cd  handywedge-master/handywedge-openidconnect && \
    /tmp/install/apache-maven-3.5.4/bin/mvn package && \
    cp /tmp/install/handywedge-master/handywedge-openidconnect/target/openidconnect.war $CATALINA_HOME/webapps/

# 上書き可能環境変数
ENV LOG_LEVEL=info
ENV AZURE_TENANT_ID=
ENV CLIENT_ID=
ENV RELYING_PARTY_URL=

# インストーラー等削除
WORKDIR /root
RUN rm -rf /tmp/install/ && \
    rm -rf .m2


# CMD ["$CATALINA_HOME/bin/catalina.sh", "run"]
