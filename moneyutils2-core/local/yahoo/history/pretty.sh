#!/bin/sh

symbol=CELG

cat ${symbol}.json  | python -m json.tool > ${symbol}-pretty.json

