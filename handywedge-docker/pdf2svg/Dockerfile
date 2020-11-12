FROM maven:3.6.3-openjdk-8 AS build-stage

# warビルド
RUN mkdir /tmp/install
WORKDIR /tmp/install
COPY handywedge-pdftosvg-rest/ /tmp/install/handywedge-pdftosvg-rest/
RUN cd handywedge-pdftosvg-rest && \
    mvn clean package

FROM tomcat:8.5.59-jdk8-openjdk

ENV DEBIAN_FRONTEND noninteractive

# ロケール設定
RUN rm -rf /var/lib/apt/lists/* \
  && apt-get update \
  && apt-get install -y locales tzdata curl

# ロケール設定
RUN localedef -i ja_JP -c -f UTF-8 -A /usr/share/locale/locale.alias ja_JP.UTF-8
ENV LANG ja_JP.UTF-8

# タイムゾーン設定
ENV TZ=Asia/Tokyo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# pdf2svgインストール
RUN apt-get install -y pdf2svg

# 作業用ディレクトリ作成
RUN mkdir -p /converters/pdf2svg
ENV converter_dir /converters/pdf2svg

# デプロイ
COPY --from=build-stage /tmp/install/handywedge-pdftosvg-rest/target/pdftosvg.war $CATALINA_HOME/webapps
