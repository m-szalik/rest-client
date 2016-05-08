package org.jsoftware.restclient.impl;

import org.jsoftware.restclient.RestClientResponse;

import java.io.Serializable;

public class ResponseStatusImpl implements Serializable, RestClientResponse.ResponseStatus {
    private final int code;
    private final String reason;

    public ResponseStatusImpl(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    @Override
    public int getStatusCode() {
        return code;
    }

    @Override
    public String getReasonPhrase() {
        return reason;
    }
}
