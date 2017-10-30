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
    private static final Logger LOGER = Logger.getLogger(UpdateFiDirCmd.class);

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
            LOGER.info("> START updating dir=" + dir);
            AbstractFiDir fiDir = null;
            try {
                fiDir = new DefaultFiDir(dir);
                fiDir.sendRequest();
            } catch (IOException e) {
                LOGER.error("Failed to update dir=" + dir + ". Error:" + e.getMessage());
            } finally {
                LOGER.info("< DONE updating dir=" + dir);
            }
        }
    }

}
