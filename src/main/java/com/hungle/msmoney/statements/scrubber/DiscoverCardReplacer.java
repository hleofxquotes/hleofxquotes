package com.hungle.msmoney.statements.scrubber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class DiscoverCardReplacer.
 */
public class DiscoverCardReplacer extends AbstractReplacer {

    /**
     * Instantiates a new discover card replacer.
     */
    public DiscoverCardReplacer() {
        super();
        // p = re.compile(r'(<FITID>)(.+?)(?=<)',re.IGNORECASE)
        // FITIDYYYYMMDDamt#####
        // <FITID>320111120086694629</FITID>
        // <FITID>FITID20100130-10.8500005<NAME>CVS/PHARMACY </STMTTRN>
        setPattern(Pattern.compile("(<FITID>)(.+?)(?=<)", Pattern.CASE_INSENSITIVE));
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.scrubber.AbstractReplacer#appendReplacement(java.util.regex.Matcher, java.lang.StringBuffer)
     */
    @Override
    public void appendReplacement(Matcher matcher, StringBuffer sb) {
        matcher.appendReplacement(sb, "<FITID>XXX-TODO");
    }

}
