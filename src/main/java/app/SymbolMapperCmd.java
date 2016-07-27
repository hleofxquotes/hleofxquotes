package app;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.le.tools.moneyutils.data.SymbolMapper;

public class SymbolMapperCmd {
    private static final Logger log = Logger.getLogger(SymbolMapperCmd.class);

    /**
     * @param args
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
