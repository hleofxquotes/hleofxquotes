package com.hungle.tools.moneyutils.currency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyCodesCmd.
 */
public class CurrencyCodesCmd {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(CurrencyCodesCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        File inFile = null;
        File outFile = null;

        if (args.length == 2) {
            inFile = new File(args[0]);
            outFile = new File(args[1]);
        } else {
            Class<CurrencyCodesCmd> clz = CurrencyCodesCmd.class;
            System.out.println("Usage: java " + clz.getName() + "currencyCodes.csv currencyCodes.txt");
            System.exit(1);
        }
        LOGGER.info("inFile=" + inFile);
        LOGGER.info("outFile=" + outFile);
        CsvReader reader = null;
        PrintWriter writer = null;
        try {
            List<String> codes = new ArrayList<String>();
            reader = new CsvReader(new BufferedReader(new FileReader(inFile)));
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            reader.readHeaders();
            while (reader.readRecord()) {
                String columnName = null;

                columnName = "Name";
                String name = reader.get(columnName);

                columnName = "Code";
                String code = reader.get(columnName);

                codes.add(code);
            }

            for (String code1 : codes) {
                for (String code2 : codes) {
                    if (code1.equals(code2)) {
                        continue;
                    }
                    if (!(code1.equals("USD") || code2.equals("USD"))) {
                        continue;
                    }
                    writer.println(code1 + code2 + "=X");
                }
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("< DONE");
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

}
