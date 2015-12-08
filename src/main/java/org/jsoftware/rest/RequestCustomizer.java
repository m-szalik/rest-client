package org.jsoftware.rest;

/**
 * @author szalik
 */
@FunctionalInterface
public interface RequestCustomizer<R> {

    void customize(R r);

}
