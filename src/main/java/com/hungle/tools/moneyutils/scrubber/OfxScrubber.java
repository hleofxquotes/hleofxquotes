package com.hungle.tools.moneyutils.scrubber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class OfxScrubber.
 */
public class OfxScrubber {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(OfxScrubber.class);

    /** The replacer. */
    private AbstractReplacer replacer;

    /**
     * Instantiates a new ofx scrubber.
     *
     * @param replacer the replacer
     */
    public OfxScrubber(AbstractReplacer replacer) {
        setReplacer(replacer);
    }

    /**
     * Scrub.
     *
     * @param inFile the in file
     * @param outFile the out file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void scrub(File inFile, File outFile) throws IOException {
        scrub(inFile, outFile, getReplacer());
    }

    /**
     * Scrub.
     *
     * @param inFile the in file
     * @param outFile the out file
     * @param replacer the replacer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void scrub(File inFile, File outFile, AbstractReplacer replacer) throws IOException {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(inFile));
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            scrub(reader, writer, replacer);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } finally {
                    reader = null;
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
    }

    /**
     * Scrub.
     *
     * @param reader the reader
     * @param writer the writer
     * @param replacer the replacer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void scrub(BufferedReader reader, PrintWriter writer, AbstractReplacer replacer) throws IOException {
        String line = null;
        while ((line = reader.readLine()) != null) {
            scrubLine(writer, replacer, line);
        }
    }

    /**
     * Scrub line.
     *
     * @param writer the writer
     * @param replacer the replacer
     * @param line the line
     */
    protected void scrubLine(PrintWriter writer, AbstractReplacer replacer, String line) {
        if (replacer != null) {
            Pattern pattern = replacer.getPattern();
            if (pattern != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher != null) {
                    line = replacer.searchAndReplace(matcher);
                }
            }
        }
        notifyScrubbedLine(line, writer);
    }

    /**
     * Notify scrubbed line.
     *
     * @param str the str
     * @param writer the writer
     */
    protected void notifyScrubbedLine(String str, PrintWriter writer) {
        if (log.isDebugEnabled()) {
            log.debug(str);
        }
        writer.println(str);
    }

    /**
     * Gets the replacer.
     *
     * @return the replacer
     */
    public AbstractReplacer getReplacer() {
        return replacer;
    }

    /**
     * Sets the replacer.
     *
     * @param replacer the new replacer
     */
    public void setReplacer(AbstractReplacer replacer) {
        this.replacer = replacer;
    }
}
