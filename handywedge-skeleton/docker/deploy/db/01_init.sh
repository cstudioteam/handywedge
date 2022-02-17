#!/bin/bash

set -e
psql -v ON_ERROR_STOP=1 -U postgres -d skeleton -f /tmp/init.sql
psql -v ON_ERROR_STOP=1 -U skeleton -d skeleton -f /tmp/ddl_hw.sql
psql -v ON_ERROR_STOP=1 -U skeleton -d skeleton -f /tmp/ddl.sql
psql -v ON_ERROR_STOP=1 -U skeleton -d skeleton -f /tmp/dml.sql