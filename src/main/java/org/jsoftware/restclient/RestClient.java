package org.jsoftware.restclient;

import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

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
     * @throws MalformedURLException if <code>url</code> is not well formated.
     */
    RestClientCall get(String url) throws MalformedURLException;


    /**
     * Prepare HEAD request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formated.
     */
    RestClientCall head(String url) throws MalformedURLException;


    /**
     * Prepare OPTIONS request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formated.
     */
    RestClientCall options(String url) throws MalformedURLException;


    /**
     * Prepare DELETE request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formated.
     */
    RestClientCall delete(String url) throws MalformedURLException;


    /**
     * Prepare POST request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formated.
     */
    RestClientDataCall post(String url) throws MalformedURLException;


    /**
     * Prepare PUT request
     * @param url url
     * @return request builder
     * @throws MalformedURLException if <code>url</code> is not well formated.
     */
    RestClientDataCall put(String url) throws MalformedURLException;


}

