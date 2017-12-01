package com.hungle.msmoney.stmt.scrubber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractReplacer.
 */
public abstract class AbstractReplacer {
    
    /** The pattern. */
    private Pattern pattern;

    /**
     * Gets the pattern.
     *
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Sets the pattern.
     *
     * @param pattern the new pattern
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Search and replace.
     *
     * @param matcher the matcher
     * @return the string
     */
    public String searchAndReplace(Matcher matcher) {
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            appendReplacement(matcher, sb);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Append replacement.
     *
     * @param matcher the matcher
     * @param sb the sb
     */
    public abstract void appendReplacement(Matcher matcher, StringBuffer sb);
    // matcher.appendReplacement(sb, "<FITID>XXX-TODO");

}
