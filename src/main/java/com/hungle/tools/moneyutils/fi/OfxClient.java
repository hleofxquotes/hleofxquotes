package com.hungle.tools.moneyutils.fi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;

import javax.crypto.SecretKey;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.hungle.tools.moneyutils.encryption.EncryptionHelper;
import com.hungle.tools.moneyutils.encryption.EncryptionHelperException;
import com.hungle.tools.moneyutils.ofx.quotes.net.HttpUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class OfxPostClient.
 */
public class OfxClient {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(OfxClient.class);

    /**
     * Send request.
     *
     * @param params
     *            the params
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void sendRequest(OfxPostClientParams params) throws IOException {
        String uriString = params.getUriString();
        if (StringUtils.isEmpty(uriString)) {
            LOGGER.warn("urlString is empty.");
            return;
        }
        URI uri = URI.create(uriString);

        CloseableHttpClient httpClient = null;
        try {
            httpClient = createHttpClient(params);

            if (uriStringContains(params, "vanguard.com")) {
                sendHeadRequest(uri, httpClient);
            }
            sendPostRequest(uri, httpClient, params);
        } finally {
            if (httpClient != null) {
                httpClient.close();
                httpClient = null;
            }
        }
    }

    private void sendHeadRequest(URI uri, CloseableHttpClient httpClient) throws ClientProtocolException, IOException {
        HttpHead httpHead = new HttpHead(uri);

        HttpResponse response = httpClient.execute(httpHead);
        HttpEntity responseEntity = response.getEntity();
        EntityUtils.consume(responseEntity);
    }

    private void sendPostRequest(URI uri, CloseableHttpClient httpClient, OfxPostClientParams params)
            throws IOException, ClientProtocolException {
        HttpPost httpPost = new HttpPost(uri);
        AbstractHttpEntity requestEntity = createRequestEntity(params);
        httpPost.setEntity(requestEntity);
        HttpResponse response = httpClient.execute(httpPost);
        handlePostResponse(params, response);
    }

    /**
     * Creates the http client.
     * 
     * @param params
     *
     * @return the closeable http client
     */
    private CloseableHttpClient createHttpClient(OfxPostClientParams params) {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        
        HttpRequestInterceptor requestInterceptor = null;
        if (uriStringContains(params, "discovercard.com")) {
            requestInterceptor = new DiscoverHttpRequestInterceptor();
        }        
        if (requestInterceptor != null) {
            httpClientBuilder = httpClientBuilder.addInterceptorLast(requestInterceptor);
        }

        return httpClientBuilder.build();
    }

    private static final boolean uriStringContains(OfxPostClientParams params, String targetString) {
        String uriString = params.getUriString();
        if (StringUtils.isNotBlank(uriString)) {
            if (uriString.contains(targetString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handle response.
     *
     * @param params
     *            the params
     * @param response
     *            the response
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void handlePostResponse(OfxPostClientParams params, HttpResponse response) throws IOException {
        HttpEntity responseEntity = response.getEntity();

        checkResponseContentType(responseEntity);

        checkResponseStatus(response, responseEntity);

        BufferedReader reader = null;
        try {
            InputStream in = responseEntity.getContent();
            Charset charset = HttpUtils.getCharset(responseEntity);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("charset=" + charset);
            }
            reader = new BufferedReader(new InputStreamReader(in, charset));

            saveResponse(reader, params);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } finally {
                    reader = null;
                }
            }
        }
    }

    /**
     * Check response status.
     *
     * @param response
     *            the response
     * @param responseEntity
     *            the response entity
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void checkResponseStatus(HttpResponse response, HttpEntity responseEntity) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("status=" + response.getStatusLine());
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new IOException("Resonse is not valid, statusCode=" + statusCode);
        }
    }

    /**
     * Check response content type.
     *
     * @param responseEntity
     *            the response entity
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void checkResponseContentType(HttpEntity responseEntity) throws IOException {
        Header contentTypeHeader = responseEntity.getContentType();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("response Content-Type: " + contentTypeHeader);
        }

        boolean strictRespContentType = false;
        if (contentTypeHeader == null) {
            if (strictRespContentType) {
                throw new IOException("Not a valid ofx response. Bad contentType=" + contentTypeHeader);
            }
        } else {
            if (strictRespContentType) {
                String contentType = contentTypeHeader.getValue();
                if (contentType.compareToIgnoreCase(AbstractFiDir.APPLICATION_X_OFX) != 0) {
                    throw new IOException("Not a valid ofx response. Bad contentType=" + contentTypeHeader);
                }
            }
        }
    }

    /**
     * Creates the request entity.
     *
     * @param params
     *            the params
     * @return the abstract http entity
     */
    protected AbstractHttpEntity createRequestEntity(OfxPostClientParams params) {
        AbstractHttpEntity requestEntity = null;
        File reqFile = params.getReqFile();
        requestEntity = new FileEntity(reqFile, ContentType.create(AbstractFiDir.APPLICATION_X_OFX));
        requestEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, AbstractFiDir.APPLICATION_X_OFX));
        return requestEntity;
    }

    /**
     * Save response.
     *
     * @param reader
     *            the reader
     * @param params
     *            the params
     * @return the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static File saveResponse(BufferedReader reader, OfxPostClientParams params) throws IOException {
        File respFile = null;
        EncryptionHelper encryptionHelper = params.getEncryptionHelper();

        if (encryptionHelper == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No encryptionHelper. Save response in plain text.");
            }
            respFile = saveResponsePlain(reader, params);
        } else {
            respFile = params.getRespFile();
            try {
                SecretKey key = encryptionHelper.getKey(respFile);
                encryptionHelper.encrypt(reader, respFile, key);
            } catch (EncryptionHelperException e) {
                throw new IOException(e);
            }
        }
        return respFile;
    }

    /**
     * Save response plain.
     *
     * @param reader
     *            the reader
     * @param params
     *            the params
     * @return the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static File saveResponsePlain(BufferedReader reader, OfxPostClientParams params) throws IOException {
        PrintWriter writer = null;
        File respFile = params.getRespFile();
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(respFile)));
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } finally {
                    writer = null;
                }
            }
        }
        return respFile;
    }

    public void checkUrl(String uriString, OfxPostClientParams params) throws IOException {
        URI uri = URI.create(uriString);

        CloseableHttpClient httpClient = null;
        try {

            httpClient = createHttpClient(params);
            sendHeadRequest(uri, httpClient);
        } finally {
            if (httpClient != null) {
                httpClient.close();
                httpClient = null;
            }
        }
    }
}
