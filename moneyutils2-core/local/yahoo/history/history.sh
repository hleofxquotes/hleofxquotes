#!/bin/sh

symbol=CELG

wget -O ${symbol}.html https://finance.yahoo.com/quote/${symbol}/history?p=${symbol}
