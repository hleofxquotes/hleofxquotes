package com.hungle.tools.moneyutils.ssl;

import java.util.concurrent.CountDownLatch;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.log4j.Logger;

class OfxHandshakeCompletedListener implements HandshakeCompletedListener {
    private static final Logger log = Logger.getLogger(OfxHandshakeCompletedListener.class);

    private final TrustStrategy trustStrategy;

    private Exception handshakeException = null;

    private final CountDownLatch latch = new CountDownLatch(1);

    OfxHandshakeCompletedListener(TrustStrategy trustStrategy) {
        this.trustStrategy = trustStrategy;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public Exception getHandshakeException() {
        return handshakeException;
    }

    public void setHandshakeException(Exception handshakeException) {
        // log.info("> setHandshakeException=" + handshakeException);

        this.handshakeException = handshakeException;
    }

    @Override
    public void handshakeCompleted(HandshakeCompletedEvent event) {
        setHandshakeException(null);
        log.info("> handshakeCompleted 1");
        log.info("getCipherSuite=" + event.getCipherSuite());
        log.info("getPeerHost=" + event.getSession().getPeerHost());

        javax.security.cert.X509Certificate[] chains;
        try {
            chains = event.getPeerCertificateChain();
            log.info("PeerCertificateChain: " + chains.length);
            int count = 0;
            for (X509Certificate chain : chains) {
                log.info("  [" + count + "], getSigAlgName=" + chain.getSigAlgName());
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
            if (log.isDebugEnabled()) {
                log.warn(e, e);
            }
        } finally {
            latch.countDown();
        }
    }
}