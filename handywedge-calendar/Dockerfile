FROM maven:3.6.3-amazoncorretto-8 AS build-stage

RUN mkdir /build && yum -y install git
WORKDIR /build

RUN git clone https://github.com/cstudioteam/handywedge.git

WORKDIR handywedge/handywedge-calendar
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
COPY deploy/logging.properties conf/
RUN rm -rf webapps/* && \
    mkdir -p conf/Catalina/localhost && \
    echo "export LANG=ja_JP.UTF-8" >> bin/setenv.sh && \
    echo "export JAVA_OPTS=-Duser.timezone=Asia/Tokyo" >> bin/setenv.sh && \
    chmod +x bin/setenv.sh && \
    sed -i -e 's/UMASK="0027"/UMASK="0022"/g' bin/catalina.sh && \
    sed -i -e 's/prefix="localhost_access_log" suffix=".txt"/prefix="catalina.localhost_access" suffix=".log" rotatable="false"/g' conf/server.xml
COPY --from=build-stage /build/handywedge/handywedge-calendar/handywedge-calendar-service/target/hw-cal.war $CATALINA_HOME/webapps

RUN rm -rf /var/cache/yum

# 上書き可能環境変数
ENV LOG_LEVEL=info

ENV PROXY_USE=false
ENV PROXY_TYPE=
ENV PROXY_HOST=
ENV PROXY_PORT=

ENV AUTH_APP_ID='ce09fc43-1a9c-4d5f-ae5f-59e574fa6a1d'
ENV AUTH_SCOPE='https://graph.microsoft.com/.default'
ENV AUTH_SECRET='7VX+Dtp_7K+1y9[hKpBMHf+ji:3Xk[_y'
ENV AUTH_TENANT='cstudioteam.onmicrosoft.com'

ENV GETSCHEDULE_USERS=5
ENV GETSCHEDULE_REQUESRS=5
ENV GETSCHEDULE_DELEGATE='xxx@cstudioteam.onmicrosoft.com'
