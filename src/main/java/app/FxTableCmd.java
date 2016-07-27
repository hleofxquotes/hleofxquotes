package app;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.ofx.quotes.FxTable;

// TODO: Auto-generated Javadoc
/**
 * The Class FxTableCmd.
 */
public class FxTableCmd {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(FxTableCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
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
