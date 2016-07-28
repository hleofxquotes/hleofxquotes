package com.hungle.tools.moneyutils.ssl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.fi.OfxPostClientParams;

public class OfxDefaultHttpClient extends DefaultHttpClient {
    private static final Logger LOGGER = Logger.getLogger(OfxDefaultHttpClient.class);

    private OfxHandshakeCompletedListener handshakeCompletedListener;

    private OfxDefaultHttpClient(ClientConnectionManager clientConnectionManager) {
        super(clientConnectionManager);
    }

    private OfxDefaultHttpClient(ClientConnectionManager clientConnectionManager, HttpParams params) {
        super(clientConnectionManager, params);
    }

    public static OfxDefaultHttpClient createHttpClient(OfxPostClientParams params) throws IOException {
        boolean allowTrustStrategy = true;

        TrustStrategy trustStrategy = params.getTrustStrategy();
        if (!allowTrustStrategy) {
            trustStrategy = null;
        }
        LOGGER.info("trustStrategy=" + trustStrategy);

        // See also: http://www.imperialviolet.org/2011/05/04/pinning.html
        final OfxHandshakeCompletedListener handshakeCompletedListener = new OfxHandshakeCompletedListener(trustStrategy);
        ClientConnectionManager clientConnectionManager = OfxDefaultHttpClient.createClientConnectionManager(handshakeCompletedListener);
        OfxDefaultHttpClient httpClient = new OfxDefaultHttpClient(clientConnectionManager);
        httpClient.handshakeCompletedListener = handshakeCompletedListener;

        // at this point, we have a client that knows how to intercept the SSL certificates
       
        // now if we need to allow ANY certificate, do that here by re-wrapping the current client with a new one that 
        // will allow all certificates.
        boolean acceptAnySslCertificate = params.getHttpProperties().isAcceptAnySslCertificate();
        LOGGER.info("acceptAnySslCertificate=" + acceptAnySslCertificate);
        if (acceptAnySslCertificate) {
            LOGGER.warn("SSL: skipping server certificate check in SSL Handshake!!!");
            try {
                httpClient = OfxDefaultHttpClient.wrapToAllowAllClient(httpClient);
            } catch (KeyManagementException e) {
                throw new IOException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new IOException(e);
            }
        }

        // HACK: because we are using handshakeCompletedListener which could be ran in another thread, we
        // need to wait via the listener's latch before allowing the request to continue.
        // will abort the request if we have error from the listener.
        httpClient.addRequestInterceptor(new CheckHandshakeStatus(handshakeCompletedListener));

        boolean useExpectContinue = true;
        if (useExpectContinue) {
            HttpParams httpParams = httpClient.getParams();
            // log.info("userAgent=" + HttpProtocolParams.getUserAgent(params));
            HttpProtocolParams.setUseExpectContinue(httpParams, false);
        }
        
        // done with decorating the client
        return httpClient;
    }

    private static ClientConnectionManager createClientConnectionManager(final OfxHandshakeCompletedListener listener) throws IOException {
        ClientConnectionManager clientConnectionManager = null;
        try {
            // using TrustStrategy seems to trigger the MD2 check. Work-around
            // using HandshakeCompletedEvent for now.
            TrustStrategy ts = null;
            SSLSocketFactory sslSocketFatory = new OfxSSLSocketFactory(ts, listener);
            
            SchemeRegistry schemeRegistry = SchemeRegistryFactory.createDefault();
            
            schemeRegistry.register(new Scheme("https", 443, sslSocketFatory));
            
            clientConnectionManager = new SingleClientConnManager(schemeRegistry);
        } catch (KeyManagementException e) {
            throw new IOException(e);
        } catch (UnrecoverableKeyException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (KeyStoreException e) {
            throw new IOException(e);
        }
    
        return clientConnectionManager;
    }

    static OfxDefaultHttpClient wrapToAllowAllClient(final OfxDefaultHttpClient base) throws KeyManagementException, NoSuchAlgorithmException {
        OfxHandshakeCompletedListener listener = base.handshakeCompletedListener;

        SSLSocketFactory sslSocketFatory = createAllowAllLSslSocketFatory(listener);

        ClientConnectionManager clientConnectionManager = base.getConnectionManager();
        SchemeRegistry schemeRegistry = clientConnectionManager.getSchemeRegistry();
        
        schemeRegistry.register(new Scheme("https", 443, sslSocketFatory));

        OfxDefaultHttpClient wrappedClient = new OfxDefaultHttpClient(clientConnectionManager, base.getParams());
        wrappedClient.handshakeCompletedListener = listener;

        return wrappedClient;
    }

    private static SSLSocketFactory createAllowAllLSslSocketFatory(OfxHandshakeCompletedListener listener) throws NoSuchAlgorithmException,
            KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        X509TrustManager tm = new AllowAllTrustManager();
        context.init(null, new TrustManager[] { tm }, null);

        X509HostnameVerifier verifier = new AllowAllHostnameVerifier();

        SSLSocketFactory sslSocketFatory = new OfxSSLSocketFactory(context, verifier, listener);
        
        return sslSocketFatory;
    }
}
