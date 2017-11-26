Sat Nov 25 18:02:37 PST 2017

  * This kit provides a work-around to help MD user to fetch quotes while the "official" extension is being worked on.

  * Download the *.zip file. Unzip to get
    ** yahooqt.mxt
    ** ycsvqserver-0.0.1-SNAPSHOT-exec.jar

  * yahooqt.mxt: is an modified version of 'moneydance_open' (from commit 88eb4c6). The only change is

YahooConnectionUSA.java
  private static final String CURRENT_PRICE_URL_BASE = "http://localhost:8080/d/quotes.csv";

  You can install this extension through the standard extension installation from file. The extension is NOT signed. So you will get the warning.

 * ycsvqserver-0.0.1-SNAPSHOT-exec.jar
  is a version of hleofxquotes 'gateway' mode. In this mode, it will start and listen on port 8080 for the 'classic Yahoo CSV quote request' and will response to MD with appropriate content.


To use:
  * Install the modified extension: yahooqt.mxt. Use it as before. The only new dependency is that whenever you need to perform the price update, you need to make sure that the hleofxquotes "gateway" is running locally.

  * To start the hleofxquotes "gateway"
     java -jar ycsvqserver-0.0.1-SNAPSHOT-exec.jar

Wait until you see
... Tomcat started on port(s): 8080 (http)

  * To stop, control-C

Happy Thanksgving. Hope this helps. 


