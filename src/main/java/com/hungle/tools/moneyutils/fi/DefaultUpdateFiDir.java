package com.hungle.tools.moneyutils.fi;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.props.HttpProperties;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultUpdateFiDir.
 */
public class DefaultUpdateFiDir extends AbstractUpdateFiDir {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(DefaultUpdateFiDir.class);

    /**
     * Instantiates a new default update fi dir.
     *
     * @param dir the dir
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public DefaultUpdateFiDir(File dir) throws IOException {
        super(dir);
    }

    /* (non-Javadoc)
     * @see com.hungle.tools.moneyutils.fi.AbstractUpdateFiDir#update(java.lang.String, java.io.File, java.io.File, com.hungle.tools.moneyutils.fi.props.HttpProperties)
     */
    @Override
    protected void update(String url, File reqFile, File respFile, HttpProperties httpProperties) throws IOException {
        OfxPostClientParams params = new OfxPostClientParams(url, reqFile, respFile, httpProperties);
        OfxPostClient ofxPostClient = new OfxPostClient();
        ofxPostClient.sendRequest(params);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created respFile=" + respFile.getAbsolutePath());
        }
    }
}
