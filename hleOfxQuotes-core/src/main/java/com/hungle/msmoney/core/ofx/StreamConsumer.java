/**
 * 
 */
package com.hungle.msmoney.core.ofx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class StreamConsumer.
 */
public class StreamConsumer implements Runnable {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(StreamConsumer.class);

    /** The in. */
    private final InputStream stream;
    
    /** The tag. */
    private String tag = null;

    private final ArrayList<String> lines;

    /**
     * Instantiates a new stream consumer.
     *
     * @param stream the stdin
     * @param tag the tag
     */
    public StreamConsumer(InputStream stream, String tag) {
        this.stream = stream;
        this.tag = tag;
        this.lines = new ArrayList<String>();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("> START consuming stream=" + tag);
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (LOGGER.isDebugEnabled()) {
                    String string = "[" + tag + "] " + line;
                    lines.add(string);
                    LOGGER.debug(string);
                }
            }
        } catch (IOException e) {
            LOGGER.warn(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("< DONE consuming stream=" + tag);
            }
        }
    }

    public ArrayList<String> getLines() {
        return lines;
    }
}