FROM centos:7

RUN  \
  yum -y update && \
  yum -y install tar gcc make \
    poppler-glib-devel cairo-devel

# ロケール設定
RUN localedef -v -c -i ja_JP -f UTF-8 ja_JP.UTF-8; echo "";
ENV LANG=ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
  ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime


#RUN yum -y install pdf2svg
WORKDIR /tmp/install
RUN curl -OL https://github.com/dawbarton/pdf2svg/archive/v0.2.3.tar.gz
RUN tar -zxf v0.2.3.tar.gz
RUN cd pdf2svg-0.2.3 && \
  export CC=/usr/bin/gcc && \
  ./configure --bindir=/usr/bin \
              --sbindir=/usr/sbin \
              --libexecdir=/usr/libexec \
              --sysconfdir=/etc/pdf2svg \
              --localstatedir=/var/run/pdf2svg \
              --libdir=/usr/lib64  \
              --includedir=/usr/include \
              --datarootdir=/usr/share \
              --infodir=/usr/share/info \
              --localedir=/usr/share/locale \
              --mandir=/usr/share/man \
              --docdir=/usr/share/doc/pdf2svg \
   && make && make install \
   && rm -rf /tmp/*


CMD ["/bin/bash"]
