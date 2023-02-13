#!/bin/sh

set -m

dockerd & # workaround, use docker-entrypoint.sh somehow

sleep 10 # workaround, use systemd

/app/run-java.sh
