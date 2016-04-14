package org.jsoftware.restclient;

import org.jetbrains.annotations.NotNull;

import javax.xml.namespace.QName;

/**
 * Invalid content type.
 * @author szalik
 * @see RestClientResponse#json(String)
 * @see RestClientResponse#xPath(String)
 * @see RestClientResponse#xPath(String, QName)
 */
public class InvalidTypeOfContentException extends InvalidContentException {
    private final String content;

    public InvalidTypeOfContentException(String msg, String content, Throwable init) {
        super(msg, init);
        this.content = content;
    }

    @NotNull
    public String getContent() {
        return content;
    }
}
