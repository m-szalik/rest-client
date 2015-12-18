package org.jsoftware.restclient;

import java.io.IOException;

/**
 * Base request builder.
 * @author szalik
 */
public interface BaseRestClientCall<C extends BaseRestClientCall> {

    /**
     * Execute call
     * @return http response
     */
    RestClientResponse execute() throws IOException;

    /**
     * Add request parameter
     * @param name parameter name
     * @param value parameter value
     * @return self
     */
    C parameter(String name, Object value);

}
