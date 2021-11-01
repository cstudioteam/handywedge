FROM maven:3.8.3-amazoncorretto-11 AS build-stage

# warビルド
RUN mkdir /tmp/install
WORKDIR /tmp/install
ADD ./handywedge-report-service/ /tmp/install/handywedge-report-service/
RUN cd handywedge-report-service && \
    mvn clean package

FROM tomcat:10.0.12-jdk11-corretto

# ロケール言語パック設定
RUN yum -y update && \
    yum -y install glibc-langpack-ja && \
    yum clean all && \
    rm -rf /var/cache/yum

ENV LANG=ja_JP.UTF-8
ENV LC_ALL ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# tomcat
ARG java_opts
WORKDIR $CATALINA_HOME
RUN rm -rf webapps/* && \
    mkdir -p conf/Catalina/localhost && \
    echo "export CLASSPATH=$CATALINA_HOME/resources" >> bin/setenv.sh && \
    echo "export LANG=ja_JP.UTF-8" >> bin/setenv.sh && \
    echo "export JAVA_OPTS=\"-Duser.timezone=Asia/Tokyo ${java_opts}\"" >> bin/setenv.sh && \
    chmod +x bin/setenv.sh && \
    sed -i -e 's/UMASK="0027"/UMASK="0022"/g' bin/catalina.sh
COPY ./handywedge-docker/report-service/deploy/* $CATALINA_HOME/conf/
COPY --from=build-stage /tmp/install/handywedge-report-service/target/handywedge-report-service.war $CATALINA_HOME/webapps
