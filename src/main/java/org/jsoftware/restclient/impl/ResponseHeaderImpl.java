package org.jsoftware.restclient.impl;

import org.jsoftware.restclient.HttpHeader;

import java.io.Serializable;

public class ResponseHeaderImpl implements Serializable, HttpHeader {
    private final String name;
    private final String[] values;

    public ResponseHeaderImpl(String name, String[] values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return values.length > 0 ? values[values.length -1] : null;
    }

    @Override
    public String[] getValues() {
        return values;
    }
}
