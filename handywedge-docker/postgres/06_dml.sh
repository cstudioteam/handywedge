#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 -U handywedge -d handywedge_test_app -f /tmp/dml.sql
