package com.hungle.msmoney.core.ssl;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

public final class OfxSSLSocketFactory extends SSLSocketFactory {
    private static final Logger LOGGER = Logger.getLogger(OfxSSLSocketFactory.class);

    private OfxHandshakeCompletedListener handshakeCompletedListener;

    public OfxSSLSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier, OfxHandshakeCompletedListener handshakeCompletedListener) {
        super(sslContext, hostnameVerifier);
        this.handshakeCompletedListener = handshakeCompletedListener;
    }

    public OfxSSLSocketFactory(TrustStrategy ts, OfxHandshakeCompletedListener handshakeCompletedListener) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        super(ts);
        this.handshakeCompletedListener = handshakeCompletedListener;
    }

    @Override
    public Socket createSocket(HttpParams params) throws IOException {
        LOGGER.info("> createSocket");
        Socket socket = super.createSocket(params);

        SSLSocket sslSocket = (SSLSocket) socket;
        if (handshakeCompletedListener != null) {
            sslSocket.addHandshakeCompletedListener(handshakeCompletedListener);
        }

        return socket;
    }
}