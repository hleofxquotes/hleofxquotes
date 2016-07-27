package com.le.tools.moneyutils.fi;

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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.le.tools.moneyutils.encryption.EncryptionHelper;
import com.le.tools.moneyutils.encryption.EncryptionHelperException;
import com.le.tools.moneyutils.ofx.quotes.HttpUtils;
import com.le.tools.moneyutils.ssl.OfxDefaultHttpClient;

public class OfxPostClient {
    private static final Logger log = Logger.getLogger(OfxPostClient.class);

    public static final String DEFAULT_TEMPLATE_ENCODING = "UTF-8";

    public static final String RESP_FILE_OFX = "resp.ofx";

    public static final String REQ_FILE_OFX = "req.ofx";

    private static final String APPLICATION_X_OFX = "application/x-ofx";

    private AbstractFiContext fiContext = null;

    private VelocityContext velocityContext = null;

    private String templateEncoding = DEFAULT_TEMPLATE_ENCODING;

    public OfxPostClient(AbstractFiContext fiContext) {
        super();
        this.fiContext = fiContext;
        this.velocityContext = VelocityUtils.createVelocityContext(fiContext);
    }

    public static void sendRequest(OfxPostClientParams params) throws IOException {
        OfxDefaultHttpClient httpClient = OfxDefaultHttpClient.createHttpClient(params);
    
        String uriString = params.getUriString();
        URI uri = URI.create(uriString);
        boolean httpsOnly = params.getHttpProperties().getHttpsOnly();
        log.info("httpsOnly=" + httpsOnly);
        if (httpsOnly) {
            String scheme = uri.getScheme();
            if (scheme == null) {
                throw new IOException("URL must be https, url=" + uriString);
            }
            if (scheme.length() <= 0) {
                throw new IOException("URL must be https, url=" + uriString);
            }
            if (scheme.compareToIgnoreCase("https") != 0) {
                throw new IOException("URL must be https, url=" + uriString);
            }
            log.info("YES, url is https - " + uriString);
        }
    
        HttpPost httpPost = new HttpPost(uri);
    
        AbstractHttpEntity requestEntity = null;
        File reqFile = params.getReqFile();
        requestEntity = new FileEntity(reqFile, APPLICATION_X_OFX);
        requestEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_X_OFX));
        httpPost.setEntity(requestEntity);
    
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        Header contentType = responseEntity.getContentType();
    
        boolean strictRespContentType = false;
        if (contentType == null) {
            if (strictRespContentType) {
                throw new IOException("Not a valid ofx response. Bad contentType=" + contentType);
            }
        } else {
            if (strictRespContentType) {
                if (contentType.getValue().compareToIgnoreCase(APPLICATION_X_OFX) != 0) {
                    throw new IOException("Not a valid ofx response. Bad contentType=" + contentType);
                }
            }
        }
        log.info("status=" + response.getStatusLine());
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IOException("Resonse is not valid, statusCode=" + statusCode);
        }
    
        if (log.isDebugEnabled()) {
            log.debug("response Content-Type: " + responseEntity.getContentType());
        }
    
        BufferedReader reader = null;
        try {
            InputStream in = responseEntity.getContent();
            Charset charset = HttpUtils.getCharset(responseEntity);
            log.info("charset=" + charset);
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
    
        if (httpClient != null) {
            httpClient = null;
        }
    }

    void sendRequest(File fiDir) throws IOException {
        File reqFile = new File(fiDir, REQ_FILE_OFX);
        File respFile = new File(fiDir, RESP_FILE_OFX);
        VelocityUtils.mergeTemplate(velocityContext, fiContext.getTemplate(), templateEncoding, reqFile);

        try {
            // TODO
            sendRequest(new OfxPostClientParams(fiContext.getUri(), reqFile, respFile, null));
        } finally {
            log.info("reqFile=" + reqFile);
            log.info("respFile=" + respFile);
        }
    }

    private static File saveResponse(BufferedReader reader, OfxPostClientParams params) throws IOException {
        File respFile = null;
        EncryptionHelper encryptionHelper = params.getEncryptionHelper();

        if (encryptionHelper == null) {
            log.warn("No encryptionHelper. Save response in plain text.");
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
}
