package com.le.tools.moneyutils.scrubber;

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

public class OfxScrubber {
    private static final Logger log = Logger.getLogger(OfxScrubber.class);

    private AbstractReplacer replacer;

    public OfxScrubber(AbstractReplacer replacer) {
        setReplacer(replacer);
    }

    public void scrub(File inFile, File outFile) throws IOException {
        scrub(inFile, outFile, getReplacer());
    }

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

    public void scrub(BufferedReader reader, PrintWriter writer, AbstractReplacer replacer) throws IOException {
        String line = null;
        while ((line = reader.readLine()) != null) {
            scrubLine(writer, replacer, line);
        }
    }

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

    protected void notifyScrubbedLine(String str, PrintWriter writer) {
        if (log.isDebugEnabled()) {
            log.debug(str);
        }
        writer.println(str);
    }

    public AbstractReplacer getReplacer() {
        return replacer;
    }

    public void setReplacer(AbstractReplacer replacer) {
        this.replacer = replacer;
    }
}
