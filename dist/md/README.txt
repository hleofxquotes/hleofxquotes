Mon Nov 27 10:32:54 PST 2017

  * This kit provides a work-around to help MD user to fetch quotes while the "official" extension is being worked on.

  * Download the *.zip file. Unzip to get
    ** ycsvqserver-0.0.1-SNAPSHOT-exec.jar
    ** yahooqt.mxt

 * ycsvqserver-0.0.1-SNAPSHOT-exec.jar
  is a version of hleofxquotes 'gateway' mode. In this mode, it will start and listen on port 8080 for the 'classic Yahoo CSV quote request' and will response to MD with appropriate content.

  * yahooqt.mxt: is an modified version of 'moneydance_open' (from commit 88eb4c6). The two changes to set the URL to http://localhost:8080/d/quotes.csv

YahooConnectionUSA.java
  private static final String CURRENT_PRICE_URL_BASE = "http://localhost:8080/d/quotes.csv";
FXConnection.java
  private static final String CURRENT_BASE_URL = "http://localhost:8080/d/quotes.csv";

  You can install this extension through the standard extension installation from file. 
  The extension is NOT signed. So you will get the warning.

To use:
  * Install the modified extension: yahooqt.mxt. Use it as before. The only new dependency is that whenever you need to perform the price update, you need to make sure that the hleofxquotes "gateway" is running locally.
    ** Limitation: FX and price update works. Historical price update will NOT.

  * To start the hleofxquotes "gateway"
     java -jar ycsvqserver-0.0.1-SNAPSHOT-exec.jar

Wait until you see
... Tomcat started on port(s): 8080 (http)

  * To stop, control-C

Hope this helps. 

See also:
  * http://help.infinitekind.com/discussions/investments/2706-updating-share-prices
  * If you would like to see the code and perhaps build yourself, see: 
    ** branch: md-yahoo-gw 
    ** repo: https://bitbucket.org/hleofxquotes/moneydance_open/branch/md-yahoo-gw 
  * I've created a pull request for IK. It is up to them to merge if they like.
    https://bitbucket.org/infinitekind/moneydance_open/pull-requests/8/version-1003-change-prices-and-fx-url-to/diff

Common problems

## 
Q.
Installing the extension gives me warning 

A.
This extension is NOT sign by IK (hence the warning). You can review the source code and build it yourself. You will need
  * Git: to get the source code
  * Java JDK: for the javac compiler (min version 1.8) 
  * Ant's to run the build.xml

Source is at
  Repo: https://bitbucket.org/hleofxquotes/moneydance_open.git
  Branch: md-yahoo-gw (https://bitbucket.org/hleofxquotes/moneydance_open/branch/md-yahoo-gw)

##
Q.

% java -jar ycsvqserver-0.0.1-SNAPSHOT-exec.jar
Exception in thread "main" java.lang.UnsupportedClassVersionError: com/hungle/quotes/YcsvqserverApplication : Unsupported major.minor version 52.0
	at java.lang.ClassLoader.defineClass1(Native Method)
	at java.lang.ClassLoader.defineClass(ClassLoader.java:803)
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142)
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:442)
	at java.net.URLClassLoader.access$100(URLClassLoader.java:64)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:354)
	at java.net.URLClassLoader$1.run(URLClassLoader.java:348)

A.
You need Java 1.8. Something like this
% java -version
openjdk version "1.8.0_151"
OpenJDK Runtime Environment (build 1.8.0_151-b12)
OpenJDK 64-Bit Server VM (build 25.151-b12, mixed mode)

##
Q. Cannot bind/list on port 8080 when starting the hleofxquotes gateway

A. On Windows, try running in 'Administrator Command Prompt' 

## 
Q. How do I make sure the the gateway is working correctly?

A. With the gateway running, from browser, request

* Check price:
URL: http://localhost:8080/d/quotes.csv?s=IBM&f=sl1d1t1c1ohgv&e=.csv
that should download a CSV file that looks something like this

Symbol,Close,Date,Time,Change,Open,High,Low,Volume
IBM,151.89,11/27/2017,11:37AM,,151.89,151.89,151.89,0

* Check currency:
URL: http://localhost:8080/d/quotes.csv?s=EURUSD=X&f=sl1d1t1c1ohgv&e=.csv
that should download a CSV file that looks something like this

Symbol,Close,Date,Time,Change,Open,High,Low,Volume
EURUSD=X,1.19,11/27/2017,11:39AM,,1.19,1.19,1.19,0

##

Q. Historical prices are not updated.

A. This extension does not support getting historical price.

##

Q. Price is updated but currency is wrong.

A. This needs some addtional manual work (one time) 

  * First you need to figure out the price coming back is in what currency (pound, pence, euro, usd ...)
    You will need to download the GUI part of hleofxquotes.
    See: http://help.infinitekind.com/discussions/investments/2706-updating-share-prices/page/1
    Message: 17 (http://help.infinitekind.com/discussions/investments/2706-updating-share-prices/page/1#comment_44130576)

  * Once you figure out the currency for the prices from the quote source. Add the appropriate exchange in MD.
    See: http://help.infinitekind.com/discussions/investments/2706-updating-share-prices/page/2#comment_44138881
