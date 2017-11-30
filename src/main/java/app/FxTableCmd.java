package app;

import org.apache.log4j.Logger;

import com.hungle.msmoney.core.fx.FxTable;
import com.hungle.msmoney.core.fx.FxTableUtils;

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
