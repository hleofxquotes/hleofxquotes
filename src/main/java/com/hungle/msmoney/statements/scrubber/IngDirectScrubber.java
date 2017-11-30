package com.hungle.msmoney.statements.scrubber;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.ofx.OfxDate;

// TODO: Auto-generated Javadoc
/**
 * The Class IngDirectScrubber.
 */
public class IngDirectScrubber extends OfxScrubber {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(IngDirectScrubber.class);

    // <DTSTART>20110203000000.000<STMTTRN>
    /** The pattern. */
    // p = re.compile(r'(<DTSTART>.+?)(?=<)',re.IGNORECASE)
    private Pattern pattern = Pattern.compile("(<DTSTART>.+?)(?=<)", Pattern.CASE_INSENSITIVE);

    /**
     * Instantiates a new ing direct scrubber.
     *
     * @param replacer the replacer
     */
    public IngDirectScrubber(AbstractReplacer replacer) {
        super(replacer);
    }

    /**
     * Instantiates a new ing direct scrubber.
     */
    public IngDirectScrubber() {
        this(null);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.scrubber.OfxScrubber#scrubLine(java.io.PrintWriter, com.hungle.tools.moneyutils.scrubber.AbstractReplacer, java.lang.String)
     */
    @Override
    protected void scrubLine(PrintWriter writer, AbstractReplacer replacer, String line) {

        // assume that <DTSTART> and <DTEND> must be on the same line
        if ((line.indexOf("<DTSTART>") >= 0) && (line.indexOf("<DTEND>") < 0)) {
            log.info("Attempt to insert <DTEND>");
            if (log.isDebugEnabled()) {
                log.debug(line);
            }
            StringBuffer sb = new StringBuffer();
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                log.info("Found <DTSTART>");
                String now = OfxDate.getCurrentDateTime(0);
                String dtEnd = "<DTEND>" + now;
                log.info("Will insert DTEND=" + dtEnd);
                String replacement = matcher.group(1) + dtEnd;
                matcher.appendReplacement(sb, replacement);
            }
            matcher.appendTail(sb);
            line = sb.toString();
        }
        writer.println(line);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            Class clz = IngDirectScrubber.class;
            System.out.println("Usage: " + clz.getName() + " in.ofx scrubbed.ofx");
            System.exit(1);
        }

        File inFile = new File(args[0]);
        File outFile = new File(args[1]);

        log.info("inFile=" + inFile);
        log.info("outFile=" + outFile);

        OfxScrubber scrubber = new IngDirectScrubber();
        try {
            scrubber.scrub(inFile, outFile);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("outFile=" + outFile);
            log.info("> DONE");
        }
    }

}
