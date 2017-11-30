package app;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.msmoney.qs.ft.FtCsv;
import com.hungle.tools.moneyutils.csv2ofx.AbstractCsvConverter;

// TODO: Auto-generated Javadoc
/**
 * The Class FtCsvCmd.
 */
public class FtCsvCmd {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(FtCsvCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            Class<FtCsvCmd> clz = FtCsvCmd.class;
            System.out.println("Usage: java " + clz.getName() + " in.csv out.ofx");
            System.exit(1);
        }
        File inFile = new File(args[0]);
        LOGGER.info("inFile=" + inFile);
        File outFile = new File(args[1]);
        LOGGER.info("outFile=" + outFile);

        try {
            AbstractCsvConverter converter = new FtCsv();
            boolean forceGeneratingINVTRANLIST = false;
            LOGGER.info("> START");
            converter.convert(inFile, forceGeneratingINVTRANLIST, outFile);
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("< DONE");
        }
    }

}
