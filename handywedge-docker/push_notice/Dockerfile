FROM maven:3.6.3-amazoncorretto-8 AS build-stage

# warビルド
RUN mkdir /tmp/install
WORKDIR /tmp/install
COPY handywedge-pushnotice-service/ /tmp/install/handywedge-pushnotice-service/
RUN cd handywedge-pushnotice-service && \
    mvn clean package

FROM tomcat:9.0.39-jdk8-corretto

# 言語、build用JDKインストール
RUN yum -y update && \
    yum -y reinstall glibc-common && \
    yum -y install glibc-langpack-ja && \
    yum clean all

# 言語設定
ENV LANG=ja_JP.UTF-8
ENV LC_ALL ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

RUN mkdir -p $CATALINA_HOME/conf/Catalina/localhost/ && \
    mkdir -p $CATALINA_HOME/resources && \
    echo "export CLASSPATH=$CATALINA_HOME/resources" >> $CATALINA_HOME/bin/setenv.sh && \
    chmod +x $CATALINA_HOME/bin/setenv.sh

# デプロイ
COPY --from=build-stage /tmp/install/handywedge-pushnotice-service/target/hw-pushnotice.war $CATALINA_HOME/webapps