package com.hungle.msmoney.qs.net;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.msmoney.statements.fi.ResponseUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckRespFileV2Main.
 */
public class CheckRespFileV2Cmd {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(CheckRespFileV2Cmd.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        File respFile = null;

        if (args.length == 1) {
            respFile = new File(args[0]);
        } else {
            Class<CheckRespFileV2Cmd> clz = CheckRespFileV2Cmd.class;
            System.out.println("Usage: java " + clz.getName() + " resp.ofx");
            System.exit(1);
        }
        try {
            ResponseUtils.checkRespFileV2(respFile);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
    }

}
