FROM postgres:9.6

# ロケール設定
RUN localedef -v -c -i ja_JP -f UTF-8 ja_JP.UTF-8; echo "";
ENV LANG=ja_JP.UTF-8

# タイムゾーン設定
RUN rm -f /etc/localtime && \
    ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# 初期化用SQL
ADD --chown=postgres:postgres \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-test-app/sql/create_db.sql \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-test-app/sql/create_user.sql \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-test-app/sql/create_schema.sql \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-test-app/sql/create_table.sql \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-master/sql/ddl.sql \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-test-app/sql/dml.sql \
    /tmp/

# コンテナ作成時に初期化用SQLを流し込むためのシェル
ADD https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-docker/postgres/01_create_db.sh \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-docker/postgres/02_create_user.sh \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-docker/postgres/03_create_schema.sh \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-docker/postgres/04_create_table.sh \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-docker/postgres/05_create_table_fw.sh \
    https://raw.githubusercontent.com/cstudioteam/handywedge/master/handywedge-docker/postgres/06_dml.sh \
    /docker-entrypoint-initdb.d/
