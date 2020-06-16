FROM maven:3.6.3-jdk-8 AS build-stage

# warビルド
RUN mkdir /tmp/install
WORKDIR /tmp/install
ADD ./handywedge-binarystore/ /tmp/install/handywedge-binarystore/
RUN cd handywedge-binarystore && \
    mvn clean package

FROM tomcat:8.0.50-jre8

# 言語インストール
RUN apt-get update && \
    apt-get -y install task-japanese locales-all

# 言語設定
ENV LANG=ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

RUN rm -rf $CATALINA_HOME/webapps/* && \
    echo "export LANG=ja_JP.UTF-8" > /usr/local/tomcat/bin/setenv.sh && \
    echo "export JAVA_OPTS=-Duser.timezone=JST" >> /usr/local/tomcat/bin/setenv.sh && \
    chmod +x $CATALINA_HOME/bin/setenv.sh

COPY --from=build-stage /tmp/install/handywedge-binarystore/target/handywedge-binarystore.war $CATALINA_HOME/webapps

# 上書き可能環境変数
ENV LOG_LEVEL=info
ENV HW_STORAGE=S3
ENV AWS_ACCESS_KEY_ID=
ENV AWS_SECRET_KEY=

CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
