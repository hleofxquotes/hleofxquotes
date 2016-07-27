/**
 * 
 */
package com.le.tools.moneyutils.ofx.quotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class StreamConsumer implements Runnable {
    private static final Logger log = Logger.getLogger(GUI.class);

    private final InputStream in;
    private String tag = null;

    public StreamConsumer(InputStream stdin, String tag) {
        this.in = stdin;
        this.tag = tag;
    }

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("> START consuming stream=" + tag);
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (log.isDebugEnabled()) {
                    log.debug("[" + tag + "] " + line);
                }
            }
        } catch (IOException e) {
            log.warn(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("< DONE consuming stream=" + tag);
            }
        }
    }
}