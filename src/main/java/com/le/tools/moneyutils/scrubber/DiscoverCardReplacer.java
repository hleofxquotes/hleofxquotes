package com.le.tools.moneyutils.scrubber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscoverCardReplacer extends AbstractReplacer {

    public DiscoverCardReplacer() {
        super();
        // p = re.compile(r'(<FITID>)(.+?)(?=<)',re.IGNORECASE)
        // FITIDYYYYMMDDamt#####
        // <FITID>320111120086694629</FITID>
        // <FITID>FITID20100130-10.8500005<NAME>CVS/PHARMACY </STMTTRN>
        setPattern(Pattern.compile("(<FITID>)(.+?)(?=<)", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public void appendReplacement(Matcher matcher, StringBuffer sb) {
        matcher.appendReplacement(sb, "<FITID>XXX-TODO");
    }

}
