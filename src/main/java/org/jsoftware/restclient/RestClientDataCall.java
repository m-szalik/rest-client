package org.jsoftware.restclient;

import org.apache.http.entity.ContentType;

import java.io.InputStream;

/**
 * Request builder for POST and PUT http methods.
 * @author szalik
 */
public interface RestClientDataCall extends BaseRestClientCall<RestClientDataCall> {

    /**
     * Set parameters encoding
     * @param charset charse for encoding post and put parameters
     * @return self
     * @see BaseRestClientCall#parameter(String, Object)
     */
    RestClientDataCall parametersEncoding(String charset);

    /**
     * Set request body
     * @param inputStream body
     * @param contentType body content-type
     * @return self
     */
    RestClientDataCall body(InputStream inputStream, ContentType contentType);

    /**
     * Set request body
     * @param data body
     * @param contentType body content-type
     * @return self
     */
    RestClientDataCall body(byte[] data, ContentType contentType);

    /**
     * Set request body
     * @param data body
     * @param contentType body content-type
     * @return self
     */
    RestClientDataCall body(String data, ContentType contentType);

}
