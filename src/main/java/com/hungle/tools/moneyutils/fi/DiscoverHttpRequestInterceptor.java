package com.hungle.tools.moneyutils.fi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class DiscoverHttpRequestInterceptor.
 */
public class DiscoverHttpRequestInterceptor extends TransformHttpRequestInterceptor {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(DiscoverHttpRequestInterceptor.class);

    /** The allow header names. */
    private Set<String> allowHeaderNames;

    /**
     * Instantiates a new discover http request interceptor.
     */
    public DiscoverHttpRequestInterceptor() {
        super();

        allowHeaderNames = new LinkedHashSet<>();

        allowHeaderNames.add(HttpHeaders.CONTENT_TYPE);
        allowHeaderNames.add(HttpHeaders.HOST);
        allowHeaderNames.add(HttpHeaders.CONTENT_LENGTH);
        allowHeaderNames.add(HttpHeaders.CONNECTION);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hungle.tools.moneyutils.fi.TransformHttpRequestInterceptor#
     * filterHeaders(org.apache.http.HttpRequest)
     */
    @Override
    protected Header[] filterHeaders(HttpRequest httpRequest) {
        List<Header> filteredHeaders = new ArrayList<>();
        Header[] headers = httpRequest.getAllHeaders();
        for (Header header : headers) {
            Header transformRequestHeader = transformRequestHeader(header);
            if (transformRequestHeader != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("+" + transformRequestHeader);
                }
                filteredHeaders.add(transformRequestHeader);
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("-" + header);
                }
            }
        }
        headers = new Header[0];
        headers = filteredHeaders.toArray(headers);
        return headers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hungle.tools.moneyutils.fi.TransformHttpRequestInterceptor#
     * sortHeaders(org.apache.http.Header[])
     */
    @Override
    protected Header[] sortHeaders(Header[] headers) {
        List<Header> sortedHeaders = new ArrayList<Header>();
        for (String allowHeaderName : allowHeaderNames) {
            Header header = getHeader(allowHeaderName, headers);
            if (header != null) {
                sortedHeaders.add(header);
            }
        }
        headers = new Header[0];
        return sortedHeaders.toArray(headers);
    }

    /**
     * Gets the header.
     *
     * @param allowHeaderName
     *            the allow header name
     * @param headers
     *            the headers
     * @return the header
     */
    private Header getHeader(String allowHeaderName, Header[] headers) {
        for (Header header : headers) {
            if (header.getName().compareTo(allowHeaderName) == 0) {
                return header;
            }
        }
        return null;
    }

    /**
     * Transform request header.
     *
     * @param header
     *            the header
     * @return the header
     */
    private Header transformRequestHeader(Header header) {
        String name = header.getName();

        if (allowHeaderNames.contains(name)) {
            return header;
        } else {
            return null;
        }
    }
}
