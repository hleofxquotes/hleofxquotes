package com.hungle.msmoney.core.ssl;

import java.util.concurrent.CountDownLatch;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving ofxHandshakeCompleted events.
 * The class that is interested in processing a ofxHandshakeCompleted
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addOfxHandshakeCompletedListener<code> method. When
 * the ofxHandshakeCompleted event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OfxHandshakeCompletedEvent
 */
public class OfxHandshakeCompletedListener implements HandshakeCompletedListener {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(OfxHandshakeCompletedListener.class);

    /** The trust strategy. */
    private final TrustStrategy trustStrategy;

    /** The handshake exception. */
    private Exception handshakeException = null;

    /** The latch. */
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * Instantiates a new ofx handshake completed listener.
     *
     * @param trustStrategy the trust strategy
     */
    public OfxHandshakeCompletedListener(TrustStrategy trustStrategy) {
        this.trustStrategy = trustStrategy;
    }

    /**
     * Gets the latch.
     *
     * @return the latch
     */
    public CountDownLatch getLatch() {
        return latch;
    }

    /**
     * Gets the handshake exception.
     *
     * @return the handshake exception
     */
    public Exception getHandshakeException() {
        return handshakeException;
    }

    /**
     * Sets the handshake exception.
     *
     * @param handshakeException the new handshake exception
     */
    public void setHandshakeException(Exception handshakeException) {
        // log.info("> setHandshakeException=" + handshakeException);

        this.handshakeException = handshakeException;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.HandshakeCompletedListener#handshakeCompleted(javax.net.ssl.HandshakeCompletedEvent)
     */
    @Override
    public void handshakeCompleted(HandshakeCompletedEvent event) {
        setHandshakeException(null);
        LOGGER.info("> handshakeCompleted 1");
        LOGGER.info("getCipherSuite=" + event.getCipherSuite());
        LOGGER.info("getPeerHost=" + event.getSession().getPeerHost());

        javax.security.cert.X509Certificate[] chains;
        try {
            chains = event.getPeerCertificateChain();
            LOGGER.info("PeerCertificateChain: " + chains.length);
            int count = 0;
            for (X509Certificate chain : chains) {
                LOGGER.info("  [" + count + "], getSigAlgName=" + chain.getSigAlgName());
                count++;
            }

            String authType = "authType";
            java.security.cert.X509Certificate[] convertedChains = new java.security.cert.X509Certificate[chains.length];
            for (int i = 0; i < chains.length; i++) {
                convertedChains[i] = CertificateUtils.convertCertificate(chains[i]);
            }
            if (trustStrategy != null) {
                trustStrategy.isTrusted(convertedChains, authType);
            }
        } catch (Exception e) {
            setHandshakeException(e);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn(e, e);
            }
        } finally {
            latch.countDown();
        }
    }
}