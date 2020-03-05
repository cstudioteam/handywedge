FROM tomcat:9.0.31

# 言語、build用JDKインストール
RUN set -x && \
    apt update && \
    apt upgrade -y --no-install-recommends && \
    apt -y --no-install-recommends install task-japanese locales-all

# 言語設定
ENV LANG=ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# warビルド&デプロイ
WORKDIR /tmp/install
RUN rm -rf $CATALINA_HOME/webapps/* && \
    mkdir -p $CATALINA_HOME/conf/Catalina/localhost/ && \
    mkdir -p $CATALINA_HOME/resources && \
    echo "export CLASSPATH=$CATALINA_HOME/resources" >> $CATALINA_HOME/bin/setenv.sh && \
    echo "export LANG=ja_JP.UTF-8" >> $CATALINA_HOME/bin/setenv.sh && \
    echo "export JAVA_OPTS=-Duser.timezone=Asia/Tokyo" >> $CATALINA_HOME/bin/setenv.sh && \
    chmod +x $CATALINA_HOME/bin/setenv.sh && \
    curl -LO https://github.com/cstudioteam/handywedge/archive/master.zip && \
    unzip master.zip && \
    curl -O https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz && \
    tar xzf apache-maven-3.6.3-bin.tar.gz && \
    cd  handywedge-master/handywedge-pushnotice-service && \
    /tmp/install/apache-maven-3.6.3/bin/mvn package && \
    cp /tmp/install/handywedge-master/handywedge-pushnotice-service/target/hw-pushnotice.war $CATALINA_HOME/webapps/

# インストーラー等削除
WORKDIR /root
RUN rm -rf /tmp/install/ && \
    rm -rf .m2

WORKDIR /usr/local/tomcat