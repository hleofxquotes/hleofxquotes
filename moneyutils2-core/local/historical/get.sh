#!/bin/sh

s="GB00B2PLJJ36.L"

url="https://finance.yahoo.com/quote/${s}/history?p=${s}"

wget -O ${s}.html ${url}
