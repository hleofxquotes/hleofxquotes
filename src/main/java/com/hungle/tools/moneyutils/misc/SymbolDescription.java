package com.le.tools.moneyutils.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class SymbolDescription {
    private static final Logger log = Logger.getLogger(SymbolDescription.class);

    /**
     * @param args
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

        log.info("> START");
        log.info("inFileName=" + inFileName);
        log.info("outFileName=" + outFileName);
        SymbolDescription symbolDescription = new SymbolDescription();
        try {
            symbolDescription.parse(inFileName, outFileName);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

    private void parse(String inFileName, String outFileName) throws IOException {
        File inFile = new File(inFileName);
        File outFile = new File(outFileName);
        parse(inFile, outFile);
    }

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
                    log.warn(e);
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
                    log.warn("Bad format - lineNumber=" + lineNumber + ", str=" + line);
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
