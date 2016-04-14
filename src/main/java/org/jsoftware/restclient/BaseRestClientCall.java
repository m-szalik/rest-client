package org.jsoftware.restclient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Base request builder.
 * @author szalik
 */
public interface BaseRestClientCall<C extends BaseRestClientCall> {

    /**
     * Execute http call
     * @throws IOException if http request failed
     * @return http response
     */
    @NotNull RestClientResponse execute() throws IOException;

    /**
     * Add request parameter
     * @param name parameter name
     * @param value parameter value
     * @return self
     */
    @NotNull C parameter(@NotNull String name, @Nullable Object value);

    /**
     * Set http header
     * @param name header name
     * @param value header value
     * @return self
     */
    @NotNull C header(@NotNull String name, @Nullable String value);

}
