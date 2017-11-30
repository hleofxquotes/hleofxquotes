package com.hungle.msmoney.core.ssl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import org.apache.log4j.Logger;

public class SimpleSSLConnectionCmd {
    private static final Logger LOGGER = Logger.getLogger(SimpleSSLConnectionCmd.class);

    public static final String TLS = "TLS";

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            int port = 443;
            String hostname = "online.americanexpress.com";
            SSLSocket socket = null;
            
            try {
                SocketFactory socketFactory = null;

                SSLContext sslContext = createDefaultSSLContext();

                LOGGER.info("sslContext=" + sslContext);

                if (sslContext != null) {
                    socketFactory = sslContext.getSocketFactory();
                }
                if (socketFactory == null) {
                    socketFactory = SSLSocketFactory.getDefault();
                }

                socket = (SSLSocket) socketFactory.createSocket(hostname, port);

                HandshakeCompletedListener listener = new HandshakeCompletedListener() {
                    @Override
                    public void handshakeCompleted(HandshakeCompletedEvent event) {
                        LOGGER.info("> handshakeCompleted");
                        LOGGER.info("getCipherSuite=" + event.getCipherSuite());
                        X509Certificate[] chains;
                        try {
                            chains = event.getPeerCertificateChain();
                            LOGGER.info("PeerCertificateChain: " + chains.length);
                            for (X509Certificate chain : chains) {
                                LOGGER.info("getSigAlgName=" + chain.getSigAlgName());
                            }
                        } catch (SSLPeerUnverifiedException e) {
                            LOGGER.error(e, e);
                        }
                    }
                };
                socket.addHandshakeCompletedListener(listener);

                LOGGER.info("hostname=" + hostname);
                socket.startHandshake();
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
    }

    private static SSLContext createDefaultSSLContext() {
        try {
            return createSSLContext(TLS, null, null, null, null);
        } catch (Exception ex) {
            throw new IllegalStateException("Failure initializing default SSL context", ex);
        }
    }

    private static SSLContext createSSLContext(String algorithm, final KeyStore keystore, final String keystorePassword, final KeyStore truststore,
            final SecureRandom random) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        if (algorithm == null) {
            algorithm = TLS;
        }
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keystorePassword != null ? keystorePassword.toCharArray() : null);
        KeyManager[] keymanagers = kmfactory.getKeyManagers();
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(truststore);
        TrustManager[] trustmanagers = tmfactory.getTrustManagers();

        LOGGER.info("trustmanagers: " + trustmanagers.length);
        // add a custom TrustManager
        TrustManager[] newTrustmanagers = new TrustManager[trustmanagers.length + 1];
        for (int i = 0; i < trustmanagers.length; i++) {
            newTrustmanagers[i] = trustmanagers[i];
        }
        newTrustmanagers[newTrustmanagers.length - 1] = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] ax509certificate, String s) throws CertificateException {
                LOGGER.info("checkClientTrusted");
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] ax509certificate, String s) throws CertificateException {
                LOGGER.info("checkServerTrusted");
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                LOGGER.info("getAcceptedIssuers");
                return null;
            }
            
        };
        trustmanagers = newTrustmanagers;
        
        SSLContext sslcontext = SSLContext.getInstance(algorithm);
        sslcontext.init(keymanagers, trustmanagers, random);
        return sslcontext;
    }
}
