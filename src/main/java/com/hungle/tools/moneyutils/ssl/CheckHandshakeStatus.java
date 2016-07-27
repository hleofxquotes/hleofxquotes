package com.hungle.tools.moneyutils.ssl;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

final class CheckHandshakeStatus implements HttpRequestInterceptor {
    private static final Logger log = Logger.getLogger(CheckHandshakeStatus.class);

    private final OfxHandshakeCompletedListener handshakeCompletedListener;

    CheckHandshakeStatus(OfxHandshakeCompletedListener handshakeCompletedListener) {
        this.handshakeCompletedListener = handshakeCompletedListener;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        // request.setHeader(HTTP.USER_AGENT, "My-own-client");
        CountDownLatch latch = handshakeCompletedListener.getLatch();
        try {
            // make sure that SSL handshake is done.
            log.info("await for SSL handshake to complete.");
            latch.await();

            Exception handshakeException = handshakeCompletedListener.getHandshakeException();
            if (handshakeException != null) {
                log.error("> SKIP sending request, has SSL handshake error.");
                // log.error(handshakeException, handshakeException);
                throw new RuntimeException(handshakeException);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}