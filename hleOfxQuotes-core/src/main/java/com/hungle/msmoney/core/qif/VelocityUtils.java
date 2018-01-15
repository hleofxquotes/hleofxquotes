package com.hungle.msmoney.core.qif;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;

// TODO: Auto-generated Javadoc
/**
 * The Class VelocityUtils.
 */
public class VelocityUtils {

    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(VelocityUtils.class);

    static {
        VelocityUtils.initVelocity();
    }

    /**
     * Inits the velocity.
     */
    private static void initVelocity() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(props);
    }

    /**
     * Merge template.
     *
     * @param context
     *            the context
     * @param template
     *            the template
     * @param encoding
     *            the encoding
     * @param toFile
     *            the to file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void mergeTemplate(VelocityContext context, String template, String encoding, File toFile) throws IOException {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(toFile));
            Velocity.mergeTemplate(template, encoding, context, writer);
        } catch (ResourceNotFoundException e) {
            throw new IOException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                }
            }
        }
    }

}
