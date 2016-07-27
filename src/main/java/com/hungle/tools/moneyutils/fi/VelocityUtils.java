package com.le.tools.moneyutils.fi;

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

public class VelocityUtils {
    private static final Logger log = Logger.getLogger(VelocityUtils.class);
    
    public static VelocityContext createVelocityContext(AbstractFiContext fiContext) {
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

    public static void initVelocity() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(props);
    }

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
                    log.warn(e);
                }
            }
        }
    }

}
