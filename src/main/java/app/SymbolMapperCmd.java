package app;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.data.SymbolMapper;

// TODO: Auto-generated Javadoc
/**
 * The Class SymbolMapperCmd.
 */
public class SymbolMapperCmd {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(SymbolMapperCmd.class);

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        SymbolMapper mapper = null;

        if (args.length == 1) {
            mapper = SymbolMapper.loadMapperFile(args[0]);
        } else {
            mapper = SymbolMapper.loadMapperFile();
        }
    }

}
