FROM tomcat:10.0.16-jdk11-corretto

RUN yum -y update && \
    yum -y reinstall glibc-common && \
    yum -y install glibc-langpack-ja && \
    yum clean all && \
    rm -rf /var/cache/yum

ENV LANG=ja_JP.UTF-8
ENV LC_ALL ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# tomcat
WORKDIR $CATALINA_HOME
RUN rm -rf webapps/* && \
    mkdir -p conf/Catalina/localhost && \
    echo "export CLASSPATH=$CATALINA_HOME/resources" >> bin/setenv.sh && \
    echo "export LANG=ja_JP.UTF-8" >> bin/setenv.sh && \
    echo "export JAVA_OPTS=-Duser.timezone=Asia/Tokyo" >> bin/setenv.sh && \
    chmod +x bin/setenv.sh && \
    sed -i -e 's/UMASK="0027"/UMASK="0022"/g' bin/catalina.sh

# server.xmlとlogging.propertiesを用意したものに上書き
COPY ./docker/deploy/app/* $CATALINA_HOME/conf/
