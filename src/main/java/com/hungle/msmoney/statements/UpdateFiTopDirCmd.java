package com.hungle.msmoney.statements;

import java.io.File;
import java.io.FileFilter;

import org.apache.log4j.Logger;

import com.hungle.msmoney.statements.fi.AbstractFiDir;
import com.hungle.msmoney.statements.fi.DefaultFiDir;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateFiTopDirCmd.
 */
public class UpdateFiTopDirCmd {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(UpdateFiTopDirCmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            Class<UpdateFiTopDirCmd> clz = UpdateFiTopDirCmd.class;
            System.err.println("Usage: java " + clz.getName() + " fiTopDir");
            System.exit(1);
        }
        File topDir = new File(args[0]);
        if (!topDir.isDirectory()) {
            log.error("Not a directory topDir=" + topDir);
            System.exit(1);
        }

//        VelocityUtils.initVelocity();

        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname == null) {
                    return false;
                }
                String name = pathname.getName();
                if (name.startsWith(".")) {
                    return false;
                }

                if (pathname.isDirectory()) {
                    return true;
                }

                return false;
            }
        };
        File[] dirs = topDir.listFiles(filter);
        for (File dir : dirs) {
            log.info("");
            log.info("> START updating dir=" + dir);
            AbstractFiDir updater = null;
            try {
                updater = new DefaultFiDir(dir);
                updater.sendRequest();
            } catch (Exception e) {
                log.error("Failed to update dir=" + dir + ". Error:" + e.getMessage());
            } finally {
                log.info("< DONE updating dir=" + dir);
            }
        }
    }

}
