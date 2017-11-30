package com.hungle.msmoney.qs.net;

import java.util.UUID;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateRandomUUID.
 */
public class CreateRandomUUID {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(CreateRandomUUID.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        int max = 10;
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            log.info(uuid);
        }
    }

}
