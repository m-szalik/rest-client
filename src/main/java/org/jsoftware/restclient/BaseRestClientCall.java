package org.jsoftware.restclient;

import java.io.IOException;

/**
 */
public interface BaseRestClientCall<C extends BaseRestClientCall> {

    RestClientResponse execute() throws IOException;

    C parameter(String name, Object value);

}
