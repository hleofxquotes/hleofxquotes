package com.le.tools.moneyutils.csv2ofx;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;

public class Csv2OfxCmd {
    private static final Logger log = Logger.getLogger(Csv2OfxCmd.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        File csvFile = null;
        File ofxFile = null;
        File mapFile = null;

        if (args.length == 2) {
            csvFile = new File(args[0]);
            ofxFile = new File(args[1]);
        } else if (args.length == 3) {
            csvFile = new File(args[0]);
            ofxFile = new File(args[1]);
            mapFile = new File(args[2]);
        } else {
            Class<Csv2OfxCmd> clz = Csv2OfxCmd.class;
            System.out.println("Usage: java " + clz.getName() + " in.csv out.ofx [map.props]");
            System.exit(1);
        }

        log.info("csvFile=" + csvFile);
        log.info("ofxFile=" + ofxFile);
        if (mapFile != null) {
            log.info("mapFile=" + mapFile);
        }
        Csv2OfxCmd.initVelocity();

        Csv2Ofx csv2Ofx = new Csv2Ofx();
        int count = 0;
        try {
            count = csv2Ofx.convert(csvFile, ofxFile, mapFile);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            log.info("Parsed " + count + " transactions.");
            log.info("ofxFile=" + ofxFile);
            log.info("< DONE");
        }
    }

    public static void initVelocity() {
        // org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
        // loader = null;
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(props);
    }
}
