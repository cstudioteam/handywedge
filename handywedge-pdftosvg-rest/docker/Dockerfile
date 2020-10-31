FROM tomcat:8.5.59-jdk8-openjdk
#FROM ubuntu:18.04

ENV DEBIAN_FRONTEND noninteractive

# ロケール設定
RUN rm -rf /var/lib/apt/lists/* \
  && apt-get update \
  && apt-get install -y locales tzdata curl
#  && apt-get install -y locales tzdata curl cron vim

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

# モジュールリリース
ADD docker/modules/ /temp/release/
RUN cp -p /temp/release/*.war /usr/local/tomcat/webapps/

CMD ["catalina.sh", "run"]
