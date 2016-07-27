package com.hungle.tools.moneyutils.fi;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class UpdateFiDirCmd {
    private static final Logger log = Logger.getLogger(UpdateFiDirCmd.class);

    /**
     * @param args
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
            UpdateFiDir updater = new UpdateFiDir(dir);
            try {
                updater.update();
            } catch (IOException e) {
                log.error("Failed to update dir=" + dir + ". Error:" + e.getMessage());
            } finally {
                log.info("< DONE updating dir=" + dir);
            }
        }
    }

}
