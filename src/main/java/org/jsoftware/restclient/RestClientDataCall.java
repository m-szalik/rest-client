package org.jsoftware.restclient;

import org.apache.http.entity.ContentType;

import java.io.InputStream;

/**
 */
public interface RestClientDataCall extends BaseRestClientCall<RestClientDataCall> {

    RestClientDataCall setParametersEncoding(String charset);

    RestClientDataCall setData(InputStream inputStream, ContentType contentType);

    RestClientDataCall setData(byte[] data, ContentType contentType);

    RestClientDataCall setData(String data, ContentType contentType);

}
