package app;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ft.AbstractCsvConverter;
import com.le.tools.moneyutils.ft.FtCsv;

public class FtCsvCmd {
    private static final Logger log = Logger.getLogger(FtCsvCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            Class<FtCsvCmd> clz = FtCsvCmd.class;
            System.out.println("Usage: java " + clz.getName() + " in.csv out.ofx");
            System.exit(1);
        }
        File inFile = new File(args[0]);
        log.info("inFile=" + inFile);
        File outFile = new File(args[1]);
        log.info("outFile=" + outFile);

        try {
            AbstractCsvConverter converter = new FtCsv();
            boolean forceGeneratingINVTRANLIST = false;
            log.info("> START");
            converter.convert(inFile, forceGeneratingINVTRANLIST, outFile);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

}
