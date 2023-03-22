#!/bin/bash
#### This script has to be adapted to the environment you are in ####
PATH="/usr/local/bin:/usr/bin:/usr/local/sbin:/usr/sbin"

RESULT=`ps -Af | grep 'python3 /home/commander/target-data/targetdata.py' | wc -l`
if [ $RESULT = 1 ]; then
  export TDSVER=7.0
  python3 /home/commander/target-data/targetdata.py
fi
