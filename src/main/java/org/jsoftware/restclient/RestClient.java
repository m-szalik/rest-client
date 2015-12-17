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

    void setPlugins(List<RestClientPlugin> plugins);

    List<RestClientPlugin> getPlugins();

    RestClientCall get(String url) throws MalformedURLException;

    RestClientCall head(String url) throws MalformedURLException;

    RestClientCall options(String url) throws MalformedURLException;

    RestClientCall delete(String url) throws MalformedURLException;

    RestClientDataCall post(String url) throws MalformedURLException;

    RestClientDataCall put(String url) throws MalformedURLException;


}

