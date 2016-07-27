package app;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.le.tools.moneyutils.ofx.quotes.FxTable;

public class FxTableCmd {
    private static final Logger log = Logger.getLogger(FxTableCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        File file = null;

        file = new File(args[0]);
        FxTable fxTable = new FxTable();
        log.info("file=" + file.getName());
        try {
            fxTable.load(file);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }

}
