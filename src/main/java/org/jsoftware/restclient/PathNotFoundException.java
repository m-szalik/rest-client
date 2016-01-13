package org.jsoftware.restclient;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * Requested path not found.
 * @author szalik
 * @see RestClientResponse#json(String)
 * @see RestClientResponse#xPath(String)
 * @see RestClientResponse#xPath(String, QName)
 */
public class PathNotFoundException extends IOException {
    private final String path;

    public PathNotFoundException(String path, Throwable init) {
        this.path = path;
        if (init !=null) {
            this.initCause(init);
        }
    }

    public String getPath() {
        return path;
    }
}
