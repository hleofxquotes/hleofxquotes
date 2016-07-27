package com.hungle.tools.moneyutils.scrubber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractReplacer {
    private Pattern pattern;

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String searchAndReplace(Matcher matcher) {
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            appendReplacement(matcher, sb);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public abstract void appendReplacement(Matcher matcher, StringBuffer sb);
    // matcher.appendReplacement(sb, "<FITID>XXX-TODO");

}
