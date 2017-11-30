#!/usr/bin/python
#
# Author: TFB (http://thefinancebuff.com)
#
# This script retrieves price quotes for a list of stock and mutual fund ticker symbols from Yahoo! Finance.
# It creates a dummy OFX file and then imports the file to the default application associated with the .ofx extension.
# I wrote this script in order to use Microsoft Money after the quote downloading feature is disabled by Microsoft.
#
# For more information, see
#    http://thefinancebuff.com/2009/09/security-quote-script-for-microsoft-money.html

import os, time, urllib2, uuid, shlex, re, datetime
import sys

currency = "USD"

stocks = ["AMZN", 
          "GOOG", 
          "PG", 
          "YHOO"]

funds = ["VTSMX", 
         "VBMFX"]

join = str.join

def _field(tag,value):
    return "<"+tag+">"+value

def _tag(tag,*contents):
    return join("\r\n",["<"+tag+">"]+list(contents)+["</"+tag+">"])

def _date():
    return time.strftime("%Y%m%d%H%M%S",time.localtime())

def _genuuid():
    return uuid.uuid4().hex

class Security:
    """
    Encapsulate a stock or mutual fund. A Security has a ticker, a name, a price quote, and 
    the as-of date and time for the price quote. Name, price and as-of date and time are retrieved
    from Yahoo! Finance.
    """

    def __init__(self, ticker):
        self.ticker = ticker
    
    def _removeIllegalChars(self, inputString):
        pattern = re.compile("[^a-zA-Z0-9 ,.-]+")
        return pattern.sub("", inputString)
        
    def getQuote(self):
        """
        Get name, price quote, and the as-of date and time for the price quote from Yahoo! Finance.
        """
        
        url = "http://finance.yahoo.com/d/quotes.csv?s=%s&f=nl1d1t1" % self.ticker
        csv = urllib2.urlopen(url).read()
        
        # example: "Amazon.com, Inc.",78.46,"9/3/2009","4:00pm"
        # can't simply use split(",") because the security name has an embedded comma
        lexer = shlex.shlex(csv)
        lexer.whitespace = ","
        lexer.whitespace_split = True

        quote = []
        for value in lexer:
            quote.append(value.strip('"'))
        
        # ampersand character (&) is not valid in OFX
        self.name = self._removeIllegalChars(quote[0])
        self.price = quote[1]
        timeStruct = time.strptime(quote[2] + " " + quote[3], "%m/%d/%Y %I:%M%p")
        self.quoteTime = time.strftime("%Y%m%d%H%M", timeStruct) + "00.000[-5:EST]"

class OfxWriter:
    """
    Create an OFX file based on a list of stocks and mutual funds.
    """
    
    def __init__(self, stockList, mfList):
        self.stockList = stockList
        self.mfList = mfList
        
    def _signOn(self):
        """Generate signon message"""
    
        return _tag("SIGNONMSGSRSV1",
                    _tag("SONRS",
                         _tag("STATUS",
                             _field("CODE", "0"),
                             _field("SEVERITY", "INFO"),
                             _field("MESSAGE","Successful Sign On")
                         ),
                         _field("DTSERVER", _date()),
                         _field("LANGUAGE", "ENG"),
                         _field("DTPROFUP", "20010918083000"),
                         _tag("FI", _field("ORG", "broker.com"))
                     )
               )

    def _invPosList(self):
        posstock = []
        for stock in self.stockList:
            posstock.append(self._pos("stock", stock.ticker, stock.price, stock.quoteTime))

        posmf = []
        for mf in self.mfList:
            posmf.append(self._pos("mf", mf.ticker, mf.price, mf.quoteTime))
            
        return _tag("INVPOSLIST",
                    join("", posstock),
                    join("", posmf)
               )

    def _pos(self, type, ticker, price, quoteTime):
        return _tag("POS" + type.upper(),
                   _tag("INVPOS",
                       _tag("SECID",
                           _field("UNIQUEID", ticker),
                           _field("UNIQUEIDTYPE", "TICKER")
                       ),
                       _field("HELDINACCT", "CASH"),
                       _field("POSTYPE", "LONG"),
                       _field("UNITS", "1"),
                       _field("UNITPRICE", price),
                       _field("MKTVAL", price),
                       _field("DTPRICEASOF", quoteTime)
                   )
               )

    def _invStmt(self, currency):
        tomorrow = datetime.datetime.today() + datetime.timedelta(days=1)
        res = _tag("INVSTMTRS",
                   _field("DTASOF", tomorrow.strftime("%Y%m%d")),
                   _field("CURDEF", currency),
                   _tag("INVACCTFROM",
                      _field("BROKERID", "dummybroker.com"),
                      _field("ACCTID","0123456789")
                   ),
                   self._invPosList()
               )

        return self._message("INVSTMT","INVSTMT",res)

    def _message(self,msgType,trnType,response):
        return _tag(msgType+"MSGSRSV1",
                    _tag(trnType+"TRNRS",
                         _field("TRNUID",_genuuid()),
                         _tag("STATUS",
                             _field("CODE", "0"),
                             _field("SEVERITY", "INFO")
                         ),
                         _field("CLTCOOKIE","4"),
                         response
                     )
                )

    def _secList(self):
        stockinfo = []
        for stock in self.stockList:
            stockinfo.append(self._info("stock", stock.ticker, stock.name, stock.price))

        mfinfo = []
        for mf in self.mfList:
            mfinfo.append(self._info("mf", mf.ticker, mf.name, mf.price))
        

        return _tag("SECLISTMSGSRSV1",
                   _tag("SECLIST",
                        join("", stockinfo),
                        join("", mfinfo)
                   )
               )

    def _info(self, type, ticker, name, price):
        secInfo = _tag("SECINFO",
                       _tag("SECID",
                           _field("UNIQUEID", ticker),
                           _field("UNIQUEIDTYPE", "TICKER")
                       ),
                       _field("SECNAME", name),
                       _field("TICKER", ticker),
                       _field("UNITPRICE", price)
                   )
        if type.upper() == "MF":
            info = _tag(type.upper() + "INFO",
                       secInfo,
                       _field("MFTYPE", "OPENEND")
                   )
        else:
            info = _tag(type.upper() + "INFO", secInfo)

        return info
        
    
    def _header(self):
        return join("\r\n",[ "OFXHEADER:100",
                           "DATA:OFXSGML",
                           "VERSION:102",
                           "SECURITY:NONE",
                           "ENCODING:USASCII",
                           "CHARSET:1252",
                           "COMPRESSION:NONE",
                           "OLDFILEUID:NONE",
                           "NEWFILEUID:"+_genuuid(),
                           ""])

    def createContent(self, currency):
        return join("\r\n",[self._header(),
                            _tag("OFX",
                                self._signOn(),
                                self._invStmt(currency),
                                self._secList()
                            )])

    def writeFile(self, name, content):
        if 1:
            f = file(name,"w")
            f.write(content)
            f.close()
        else:
            print content
        # ...

if __name__=="__main__":
    stockList = []
    for ticker in stocks:
        sec = Security(ticker)
        sec.getQuote()
        stockList.append(sec)
        
    mfList = []
    for ticker in funds:
        sec = Security(ticker)
        sec.getQuote()
        mfList.append(sec)
        
    writer = OfxWriter(stockList, mfList)
    content = writer.createContent(currency)
    fileName = "quotes.ofx"
    writer.writeFile(fileName, content)

    #os.startfile(fileName)

