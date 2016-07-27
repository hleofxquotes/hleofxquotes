package com.le.tools.moneyutils.backup;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class SaveBackupsCmd {
    private static final Logger log = Logger.getLogger(SaveBackupsCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        File inDir = null;
        File outDir = null;
        String password = null;

        if (args.length == 2) {
            inDir = new File(args[0]);
            outDir = new File(args[1]);
            password = null;
        } else if (args.length == 3) {
            inDir = new File(args[0]);
            outDir = new File(args[1]);
            password = args[2];
        } else {
            Class<SaveBackupsCmd> clz = SaveBackupsCmd.class;
            System.out.println("Usage: java " + clz.getName() + " inDir outDir [password]");
            System.exit(1);
        }

        log.info("inDir=" + inDir);
        log.info("outDir=" + outDir);
        log.info("password=" + (password != null));

        try {
            SaveBackups cmd = new SaveBackups();
            log.info("> START");
            cmd.saveBackups(inDir, outDir, password);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("< DONE");
        }
    }
}
