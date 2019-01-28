#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 -U handywedge-app -d handywedge-db -f /tmp/create_table.sql
