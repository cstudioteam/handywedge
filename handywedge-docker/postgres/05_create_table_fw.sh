#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 -U handywedge -d handywedge-db -f /tmp/ddl.sql
