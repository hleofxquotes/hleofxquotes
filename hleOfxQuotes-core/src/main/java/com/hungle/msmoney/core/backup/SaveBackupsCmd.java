package com.hungle.msmoney.core.backup;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class SaveBackupsCmd.
 */
public class SaveBackupsCmd {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(SaveBackupsCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        File fromDir = null;
        File toDir = null;
        String password = null;

        if (args.length == 2) {
            fromDir = new File(args[0]);
            toDir = new File(args[1]);
            password = null;
        } else if (args.length == 3) {
            fromDir = new File(args[0]);
            toDir = new File(args[1]);
            password = args[2];
        } else {
            Class<SaveBackupsCmd> clz = SaveBackupsCmd.class;
            System.out.println("Usage: java " + clz.getName() + " fromDir toDir [password]");
            System.exit(1);
        }

        LOGGER.info("fromDir=" + fromDir);
        LOGGER.info("toDir=" + toDir);
        LOGGER.info("password=" + (password != null));

        try {
            SaveBackups cmd = new SaveBackups();
            LOGGER.info("> START");
            cmd.saveBackups(fromDir, toDir, password);
        } catch (IOException e) {
            LOGGER.error(e, e);
        } finally {
            LOGGER.info("< DONE");
        }
    }
}
