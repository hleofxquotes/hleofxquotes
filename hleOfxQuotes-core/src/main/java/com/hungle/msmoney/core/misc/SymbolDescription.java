package com.hungle.msmoney.core.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class SymbolDescription.
 */
public class SymbolDescription {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(SymbolDescription.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        String inFileName = null;
        String outFileName = null;
        if (args.length == 2) {
            inFileName = args[0];
            outFileName = args[1];
        } else {
            Class<SymbolDescription> clz = SymbolDescription.class;
            System.out.println("Usage: java " + clz.getName() + " inFile.txt outFile.txt");
            System.exit(1);
        }

        LOGGER.info("> START");
        LOGGER.info("inFileName=" + inFileName);
        LOGGER.info("outFileName=" + outFileName);
        SymbolDescription symbolDescription = new SymbolDescription();
        try {
            symbolDescription.parse(inFileName, outFileName);
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("< DONE");
        }
    }

    /**
     * Parses the.
     *
     * @param inFileName the in file name
     * @param outFileName the out file name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void parse(String inFileName, String outFileName) throws IOException {
        File inFile = new File(inFileName);
        File outFile = new File(outFileName);
        parse(inFile, outFile);
    }

    /**
     * Parses the.
     *
     * @param inFile the in file
     * @param outFile the out file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void parse(File inFile, File outFile) throws IOException {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(inFile));
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            parse(reader, writer);
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
     * Parses the.
     *
     * @param reader the reader
     * @param writer the writer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void parse(BufferedReader reader, PrintWriter writer) throws IOException {
        String line = null;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            if (lineNumber <= 1) {
                continue;
            }
            if (line.length() <= 0) {
                continue;
            }
            if (line.charAt(0) == '#') {
                continue;
            }

            int index = line.indexOf('\t');
            if (index <= 0) {
                index = line.indexOf(' ');
                if (index <= 0) {
                    LOGGER.warn("Bad format - lineNumber=" + lineNumber + ", str=" + line);
                    continue;
                }
            }

            String leftSide = line.substring(0, index);
            leftSide = leftSide.trim();
            String rightSide = line.substring(index);
            rightSide = rightSide.trim();

            // log.info(leftSide);
            writer.println(leftSide);
        }

    }

}
