I am in California (PST) time

##
quotes-test-01.ofx

  All the dates has 12th day of month in GMT to check if Money will handle GMT conversion correctly or not.

  Expect: price wil be imported with 11th date.

      <DTSERVER>20171112185717</DTSERVER>
Sun Nov 12 18:57:17 UTC 2017
Sun Nov 12 10:57:17 PST 2017

        <DTASOF>20171112010001</DTASOF>
Sun Nov 12 01:00:01 UTC 2017
Sat Nov 11 17:00:01 PST 2017

              <DTPRICEASOF>20171112010001</DTPRICEASOF>
Sun Nov 12 01:00:01 UTC 2017
Sat Nov 11 17:00:01 PST 2017

          <DTASOF>20171112010001</DTASOF>
Sun Nov 12 01:00:01 UTC 2017
Sat Nov 11 17:00:01 PST 2017

Money imports price with date 11/11/2017

This shows Money is handling the GMT time conversion correctly.

##
quotes-test-02.ofx

  Each of the four dates has 12th, 11th, 10th and 09th of the month in GMT, check to see which date values will be used

      <DTSERVER>20171112185717</DTSERVER>
Sun Nov 12 18:57:17 UTC 2017
Sun Nov 12 10:57:17 PST 2017

        <DTASOF>20171111010001</DTASOF>
Sat Nov 11 01:00:01 UTC 2017
Fri Nov 10 17:00:01 PST 2017

              <DTPRICEASOF>20171110010001</DTPRICEASOF>
Fri Nov 10 01:00:01 UTC 2017
Thu Nov  9 17:00:01 PST 2017

          <DTASOF>20171109010001</DTASOF>
Thu Nov  9 01:00:01 UTC 2017
Wed Nov  8 17:00:01 PST 2017

Money imports price with date 11/10/2017. This shows Money is using the INVSTMTRS.DTASOF value to set the price date.

