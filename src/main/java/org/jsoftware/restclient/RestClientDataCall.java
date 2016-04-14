package org.jsoftware.restclient;

import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/**
 * Request builder for POST and PUT http methods.
 * @author szalik
 */
public interface RestClientDataCall extends BaseRestClientCall<RestClientDataCall> {

    /**
     * Set parameters encoding
     * @param charset charset for encoding post and put parameters
     * @return self
     * @see BaseRestClientCall#parameter(String, Object)
     */
    @NotNull RestClientDataCall parametersEncoding(@NotNull String charset);

    /**
     * Set request body
     * @param inputStream body
     * @param contentType body content-type
     * @return self
     */
    @NotNull RestClientDataCall body(@NotNull InputStream inputStream, @NotNull ContentType contentType);

    /**
     * Set request body
     * @param data body
     * @param contentType body content-type
     * @return self
     */
    @NotNull RestClientDataCall body(@NotNull byte[] data, @NotNull ContentType contentType);

    /**
     * Set request body
     * @param data body
     * @param contentType body content-type
     * @return self
     */
    @NotNull RestClientDataCall body(@NotNull String data, @NotNull ContentType contentType);

}
