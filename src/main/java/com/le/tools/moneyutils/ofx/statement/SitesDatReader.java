package com.le.tools.moneyutils.ofx.statement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class SitesDatReader {
    private static final Logger log = Logger.getLogger(SitesDatReader.class);

    public SitesDatReader(File inFile) throws IOException {
        parse(inFile);
    }

    private void parse(File inFile) throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(inFile));
            parse(reader);
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
        }

    }

    private void parse(BufferedReader reader) throws IOException {
        String line = null;

        while ((line = reader.readLine()) != null) {
            if (line.length() <= 0) {
                continue;
            }
            line = line.trim();

            if (line.charAt(0) == '#') {
                continue;
            }

            if (line.compareToIgnoreCase("<site>") == 0) {
                tagStartSite(line, reader);
            } else if (line.compareToIgnoreCase("</site>") == 0) {
                tagEndSite(line);
            } else if (line.compareToIgnoreCase("<stocks>") == 0) {
                tagStartStocks(line, reader);
            } else if (line.compareToIgnoreCase("</stocks>") == 0) {
                tagEndStocks(line);
            } else if (line.compareToIgnoreCase("<funds>") == 0) {
                tagStartFunds(line, reader);
            } else if (line.compareToIgnoreCase("</funds>") == 0) {
                tagEndFunds(line);
            } else {
                String[] tokens = line.split(":");
                if (tokens == null) {
                    log.warn("Malformed line=" + line);
                    continue;
                }
                if (tokens.length != 2) {
                    log.warn("Malformed line=" + line);
                    continue;
                }
            }
        }

    }

    private void tagEndFunds(String line) {
        // TODO Auto-generated method stub

    }

    private void tagStartFunds(String line, BufferedReader reader) {
        // TODO Auto-generated method stub

    }

    private void tagEndStocks(String line) {
        // TODO Auto-generated method stub

    }

    private void tagStartStocks(String line, BufferedReader reader) {
        // TODO Auto-generated method stub

    }

    private void tagEndSite(String line) {
        // TODO Auto-generated method stub

    }

    private void tagStartSite(String line, BufferedReader reader) {
        // TODO Auto-generated method stub

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        File inFile = null;
        SitesDatReader sitesDat = null;

        try {
            sitesDat = new SitesDatReader(inFile);
        } catch (IOException e) {
            log.error(e, e);
        }

    }

}
