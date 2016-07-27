package app;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.data.SymbolMapper;

// TODO: Auto-generated Javadoc
/**
 * The Class SymbolMapperCmd.
 */
public class SymbolMapperCmd {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(SymbolMapperCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        File file = new File("mapper.csv");
        log.info("> START");
        log.info("file=" + file);
        SymbolMapper mapper = new SymbolMapper();
        try {
            mapper.load(file);
        } catch (IOException e) {
            log.error(e);
        } finally {
            log.info("< DONE");
        }
    }

}
