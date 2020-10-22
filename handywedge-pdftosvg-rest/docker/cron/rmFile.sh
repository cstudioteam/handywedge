#!/bin/sh

# --------------------------------------------------
# ファイル更新日時が指定分数を越えたファイルを削除
# --------------------------------------------------
# 引数1：日
# --------------------------------------------------

PARAM_MIN_NUM=$1

LOG_FILE=/converters/logs/pdftosvg_rmfile.log

echo "`date +'%Y-%m-%d %H:%M:%S.%3N'`: Starting Job." 1>> ${LOG_FILE}
find /converters/pdf2svg -name "*.pdf" -o -name "*.svg" -type f -mmin +${PARAM_MIN_NUM} | xargs rm -f >> ${LOG_FILE}
echo "`date +'%Y-%m-%d %H:%M:%S.%3N'`: Job ended." 1>> ${LOG_FILE}

exit 0
