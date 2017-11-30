package com.hungle.msmoney.core.ssl;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckHandshakeStatus.
 */
public final class CheckHandshakeStatus implements HttpRequestInterceptor {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(CheckHandshakeStatus.class);

    /** The handshake completed listener. */
    private final OfxHandshakeCompletedListener handshakeCompletedListener;

    /**
     * Instantiates a new check handshake status.
     *
     * @param handshakeCompletedListener the handshake completed listener
     */
    public CheckHandshakeStatus(OfxHandshakeCompletedListener handshakeCompletedListener) {
        this.handshakeCompletedListener = handshakeCompletedListener;
    }

    /* (non-Javadoc)
     * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest, org.apache.http.protocol.HttpContext)
     */
    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        // request.setHeader(HTTP.USER_AGENT, "My-own-client");
        CountDownLatch latch = handshakeCompletedListener.getLatch();
        try {
            // make sure that SSL handshake is done.
            LOGGER.info("await for SSL handshake to complete.");
            latch.await();

            Exception handshakeException = handshakeCompletedListener.getHandshakeException();
            if (handshakeException != null) {
                LOGGER.error("> SKIP sending request, has SSL handshake error.");
                // log.error(handshakeException, handshakeException);
                throw new RuntimeException(handshakeException);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}