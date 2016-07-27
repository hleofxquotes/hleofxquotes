package com.hungle.tools.moneyutils.fi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.props.PropertiesFIContext;

public class OfxPostClientCmd {
    private static final Logger log = Logger.getLogger(OfxPostClientCmd.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            Class<OfxPostClientCmd> clz = OfxPostClientCmd.class;
            System.err.println("Usage: java " + clz.getName() + " fiDir");
            System.exit(1);
        }

        VelocityUtils.initVelocity();

        for (String arg : args) {
            try {
                execute(arg);
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private static void execute(String fiDirName) throws IOException {
        File fiDir = new File(fiDirName);
        if (!fiDir.isDirectory()) {
            throw new IOException("Not a directory, fiDir=" + fiDir);
        }

        File propsFile = new File(fiDir, UpdateFiDir.DEFAULT_PROPERTIES_FILENAME);

        log.info("> START");
        Properties props = new Properties();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(propsFile));
            props.load(reader);

            AbstractFiContext fiContext = new PropertiesFIContext(props);
            OfxPostClient ofClient = new OfxPostClient(fiContext);
            ofClient.sendRequest(fiDir);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }

}
