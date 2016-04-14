package org.jsoftware.restclient;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;

/**
 * @author szalik
  */
public interface RestClient extends AutoCloseable {

    /**
     * Prepare GET request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    @NotNull RestClientCall get(@NotNull String url) throws MalformedURLException;


    /**
     * Prepare HEAD request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    @NotNull RestClientCall head(@NotNull String url) throws MalformedURLException;


    /**
     * Prepare OPTIONS request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    @NotNull RestClientCall options(@NotNull String url) throws MalformedURLException;


    /**
     * Prepare DELETE request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    @NotNull RestClientCall delete(@NotNull String url) throws MalformedURLException;


    /**
     * Prepare POST request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    @NotNull RestClientDataCall post(@NotNull String url) throws MalformedURLException;


    /**
     * Prepare PUT request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    @NotNull RestClientDataCall put(@NotNull String url) throws MalformedURLException;


}

