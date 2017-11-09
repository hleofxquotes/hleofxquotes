package app;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.gui.FxTableUtils;
import com.hungle.tools.moneyutils.ofx.quotes.FxTable;

// TODO: Auto-generated Javadoc
/**
 * The Class FxTableCmd.
 */
public class FxTableCmd {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(FxTableCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        FxTable fxTable = null;
        
        if (args.length == 1) {
            fxTable = FxTableUtils.loadFxFile(args[0]);
        } else {
            fxTable = FxTableUtils.loadFxFile();
        }
    }

}
