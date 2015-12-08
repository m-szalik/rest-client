package org.jsoftware.rest;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.io.IOException;

/**
 * @author szalik
  */
public interface HttpClient extends AutoCloseable {

    void setPlugins(HttpClientPlugin... plugins);

    HttpClientPlugin[] getPlugins();

    ClientResponse get(String url, RequestCustomizer<HttpGet> customizer, NameValuePair... parameters) throws IOException;

    ClientResponse delete(String url, RequestCustomizer<HttpDelete> customizer, NameValuePair... parameters) throws IOException;

    ClientResponse post(String url, RequestCustomizer<HttpPost> customizer, NameValuePair... parameters) throws IOException;

    ClientResponse put(String url, RequestCustomizer<HttpPut> customizer, NameValuePair... parameters) throws IOException;

    ClientResponse post(String url, RequestCustomizer<HttpPost> customizer, String data) throws IOException;

    ClientResponse put(String url, RequestCustomizer<HttpPut> customizer, String data) throws IOException;

    default ClientResponse get(String url, NameValuePair... parameters) throws IOException {
        return get(url, null, parameters);
    }

    default ClientResponse delete(String url, NameValuePair... parameters) throws IOException {
        return delete(url, null, parameters);
    }

    default ClientResponse post(String url, NameValuePair... parameters) throws IOException {
        return post(url, null, parameters);
    }

    default ClientResponse put(String url, NameValuePair... parameters) throws IOException {
        return put(url, null, parameters);
    }

    default ClientResponse post(String url, String data) throws IOException {
        return post(url, null, data);
    }

    default ClientResponse put(String url, String data) throws IOException {
        return put(url, null, data);
    }

}

