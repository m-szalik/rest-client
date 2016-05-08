package org.jsoftware.restclient;

/**
 * @author szalik
 */
public interface HttpHeader {
    String getName();
    String getValue();
    String[] getValues();
}
