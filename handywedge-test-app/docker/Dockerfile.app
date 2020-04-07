FROM maven:3.6.3-amazoncorretto-8 AS build-stage

RUN mkdir /build && yum -y install git
WORKDIR /build

ARG branch="master"

RUN git clone -b ${branch} https://github.com/cstudioteam/handywedge.git
WORKDIR handywedge/handywedge-dependencies
RUN mvn install
WORKDIR ../handywedge-master
RUN mvn install
WORKDIR ../handywedge-test-app
RUN mvn package

FROM tomcat:9.0.33-jdk8-corretto

RUN yum -y update && \
    yum -y reinstall glibc-common && \
    yum -y install glibc-langpack-ja && \
    yum clean all

ENV LANG=ja_JP.UTF-8
ENV LC_ALL ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# tomcat
WORKDIR $CATALINA_HOME
RUN rm -rf webapps/* && \
    mkdir -p conf/Catalina/localhost && \
    echo "export LANG=ja_JP.UTF-8" >> bin/setenv.sh && \
    echo "export JAVA_OPTS=-Duser.timezone=Asia/Tokyo" >> bin/setenv.sh && \
    chmod +x bin/setenv.sh && \
    sed -i -e 's/UMASK="0027"/UMASK="0022"/g' bin/catalina.sh && \
    sed -i -e 's/prefix="localhost_access_log" suffix=".txt"/prefix="catalina.localhost_access" suffix=".log"/g' conf/server.xml
COPY --from=build-stage /build/handywedge/handywedge-test-app/target/handywedge-test-app.war $CATALINA_HOME/webapps

RUN rm -rf /var/cache/yum
