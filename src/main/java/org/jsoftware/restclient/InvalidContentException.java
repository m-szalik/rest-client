package org.jsoftware.restclient;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * Invalid content type.
 * @author szalik
 * @see RestClientResponse#json(String)
 * @see RestClientResponse#xPath(String)
 * @see RestClientResponse#xPath(String, QName)
 */
public class InvalidContentException extends IOException {
    private final String content;

    public InvalidContentException(String msg, String content, Throwable init) {
        super(msg);
        this.content = content;
        if (init !=null) {
            this.initCause(init);
        }
    }

    public String getContent() {
        return content;
    }
}
