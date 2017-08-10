package com.hungle.tools.moneyutils.fi;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateFiDirCmd.
 */
public class UpdateFiDirCmd {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(UpdateFiDirCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Class<UpdateFiDirCmd> clz = UpdateFiDirCmd.class;
            System.err.println("Usage: java " + clz.getName() + " fiDir1 ...");
            System.exit(1);
        }
        VelocityUtils.initVelocity();

        for (String arg : args) {
            File dir = new File(arg);
            log.info("> START updating dir=" + dir);
            AbstractUpdateFiDir updater = null;
            try {
                updater = new DefaultUpdateFiDir(dir);
                updater.update();
            } catch (IOException e) {
                log.error("Failed to update dir=" + dir + ". Error:" + e.getMessage());
            } finally {
                log.info("< DONE updating dir=" + dir);
            }
        }
    }

}
