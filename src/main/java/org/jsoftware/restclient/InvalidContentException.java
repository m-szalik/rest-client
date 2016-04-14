package org.jsoftware.restclient;

import java.io.IOException;

/**
 * General exception for response's invalid content.
 * @author szalik
 */
public abstract class InvalidContentException extends IOException {

    protected InvalidContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
