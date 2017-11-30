#!/bin/sh

s="IBM:NYQ"
url="https://markets.ft.com/data/equities/tearsheet/summary?s=${s}"

wget -O ${s}.html ${url}
