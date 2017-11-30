package com.hungle.tools.moneyutils.fi;

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

import com.hungle.tools.moneyutils.fi.props.PropertiesUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class VelocityUtils.
 */
public class VelocityUtils {
    
    /** The Constant log. */
    private static final Logger LOGGER = Logger.getLogger(VelocityUtils.class);
    
    /**
     * Creates the velocity context.
     *
     * @param fiContext the fi context
     * @return the velocity context
     */
    private static VelocityContext createVelocityContext(AbstractFiContext fiContext) {
        VelocityContext context = null;
    
        context = new VelocityContext();
        context.put("ENCODING", fiContext.getEncoding());
        context.put("NEWFILEUID", fiContext.getNewFileUid());
        context.put("DTCLIENT", fiContext.getDtClient());
        context.put("USERID", fiContext.getUserId());
        context.put("USERPASS", fiContext.getUserPass());
        context.put("LANGUAGE", fiContext.getLanguage());
        context.put("ORG", fiContext.getOrg());
        context.put("TRNUID", fiContext.getTrnUid());
        context.put("CLTCOOKIE", fiContext.getCltCookie());
        context.put("BROKERID", fiContext.getBrokerId());
        context.put("ACCTID", fiContext.getAcctId());
        context.put("DTSTART", fiContext.getDtStart());
        context.put("DTASOF", fiContext.getDtAsOf());
        return context;
    }

    /**
     * Inits the velocity.
     */
    static void initVelocity() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(props);
    }

    /**
     * Merge template.
     *
     * @param context the context
     * @param template the template
     * @param encoding the encoding
     * @param toFile the to file
     * @throws IOException Signals that an I/O exception has occurred.
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

    /**
     * Gets the template from context.
     *
     * @param context the context
     * @return the template from context
     */
    static String getTemplateNameFromContext(VelocityContext context) {
        String requestType = PropertiesUtils.getRequestType(context);
        if (PropertiesUtils.isNull(requestType)) {
            LOGGER.error("Cannot create template, requestType is null");
            return null;
        }
        com.hungle.tools.moneyutils.fi.props.OFX ofx = PropertiesUtils.getOfx(context);
        if (ofx == null) {
            LOGGER.error("Cannot create template, OFX object is null");
            return null;
        }
        String version = ofx.getVersion();
        if (PropertiesUtils.isNull(version)) {
            LOGGER.error("Cannot create template, OFX version is null");
        }
        return requestType + "-v" + version + ".vm";
    }

}
