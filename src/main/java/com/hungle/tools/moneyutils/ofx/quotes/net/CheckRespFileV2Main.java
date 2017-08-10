package com.hungle.tools.moneyutils.ofx.quotes.net;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.AbstractUpdateFiDir;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckRespFileV2Main.
 */
public class CheckRespFileV2Main {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(CheckRespFileV2Main.class);

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
            Class<CheckRespFileV2Main> clz = CheckRespFileV2Main.class;
            System.out.println("Usage: java " + clz.getName() + " resp.ofx");
            System.exit(1);
        }
        try {
            AbstractUpdateFiDir.checkRespFileV2(respFile);
        } catch (IOException e) {
            log.error(e, e);
        }
    }

}
