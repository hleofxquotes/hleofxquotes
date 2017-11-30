#!/bin/sh

s="IE00BYY18M47:GBP"
url="https://markets.ft.com/data/funds/tearsheet/summary?s=${s}"

wget -O ${s}.html ${url}
