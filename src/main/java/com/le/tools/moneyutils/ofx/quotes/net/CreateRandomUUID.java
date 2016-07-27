package com.le.tools.moneyutils.ofx.quotes.net;

import java.util.UUID;

import org.apache.log4j.Logger;

public class CreateRandomUUID {
    private static final Logger log = Logger.getLogger(CreateRandomUUID.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        int max = 10;
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            log.info(uuid);
        }
    }

}
