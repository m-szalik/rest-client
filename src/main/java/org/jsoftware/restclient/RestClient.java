package org.jsoftware.restclient;

import java.net.MalformedURLException;
import java.util.List;

/**
 * @author szalik
  */
public interface RestClient extends AutoCloseable {

    /**
     * @param plugins plugins to be used
     */
    void setPlugins(List<RestClientPlugin> plugins);

    /**
     * @return list of enabled plugins
     */
    List<RestClientPlugin> getPlugins();

    /**
     * Prepare GET request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    RestClientCall get(String url) throws MalformedURLException;


    /**
     * Prepare HEAD request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    RestClientCall head(String url) throws MalformedURLException;


    /**
     * Prepare OPTIONS request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    RestClientCall options(String url) throws MalformedURLException;


    /**
     * Prepare DELETE request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    RestClientCall delete(String url) throws MalformedURLException;


    /**
     * Prepare POST request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    RestClientDataCall post(String url) throws MalformedURLException;


    /**
     * Prepare PUT request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formatted.
     */
    RestClientDataCall put(String url) throws MalformedURLException;


}

