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
  * I've created a pull request for IK. It is up to them to merge if they like.
