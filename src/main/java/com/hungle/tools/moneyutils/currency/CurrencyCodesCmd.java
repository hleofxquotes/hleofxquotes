package com.le.tools.moneyutils.currency;

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

public class CurrencyCodesCmd {
    private static final Logger log = Logger.getLogger(CurrencyCodesCmd.class);

    /**
     * @param args
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
        log.info("inFile=" + inFile);
        log.info("outFile=" + outFile);
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
            log.error(e, e);
        } finally {
            log.info("< DONE");
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
