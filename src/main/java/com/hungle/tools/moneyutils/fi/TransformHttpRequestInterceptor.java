package com.hungle.tools.moneyutils.fi;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class TransformHttpRequestInterceptor.
 */
public class TransformHttpRequestInterceptor implements HttpRequestInterceptor {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(TransformHttpRequestInterceptor.class);

    /* (non-Javadoc)
     * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest, org.apache.http.protocol.HttpContext)
     */
    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
        LOGGER.info("> HttpRequestInterceptor");

        Header[] headers = filterHeaders(httpRequest);
        headers = sortHeaders(headers);
        
        LOGGER.info("headers.length=" + headers.length);
        httpRequest.setHeaders(headers);
    }

    /**
     * Sort headers.
     *
     * @param headers the headers
     * @return the header[]
     */
    protected Header[] sortHeaders(Header[] headers) {
        return headers;
    }

    /**
     * Filter headers.
     *
     * @param httpRequest the http request
     * @return the header[]
     */
    protected Header[] filterHeaders(HttpRequest httpRequest) {
        Header[] headers = httpRequest.getAllHeaders();
        return headers;
    }

}