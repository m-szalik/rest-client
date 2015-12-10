package org.jsoftware.rest;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;

/**
 * @author szalik
  */
public interface RestClient extends AutoCloseable {

    void setPlugins(List<HttpClientPlugin> plugins);

    List<HttpClientPlugin> getPlugins();

    RestClientResponse get(String url, RequestCustomizer<HttpGet> customizer, NameValuePair... parameters) throws IOException;

    RestClientResponse delete(String url, RequestCustomizer<HttpDelete> customizer, NameValuePair... parameters) throws IOException;

    RestClientResponse post(String url, RequestCustomizer<HttpPost> customizer, NameValuePair... parameters) throws IOException;

    RestClientResponse put(String url, RequestCustomizer<HttpPut> customizer, NameValuePair... parameters) throws IOException;

    RestClientResponse post(String url, RequestCustomizer<HttpPost> customizer, String data) throws IOException;

    RestClientResponse put(String url, RequestCustomizer<HttpPut> customizer, String data) throws IOException;

    default RestClientResponse get(String url, NameValuePair... parameters) throws IOException {
        return get(url, null, parameters);
    }

    default RestClientResponse delete(String url, NameValuePair... parameters) throws IOException {
        return delete(url, null, parameters);
    }

    default RestClientResponse post(String url, NameValuePair... parameters) throws IOException {
        return post(url, null, parameters);
    }

    default RestClientResponse put(String url, NameValuePair... parameters) throws IOException {
        return put(url, null, parameters);
    }

    default RestClientResponse post(String url, String data) throws IOException {
        return post(url, null, data);
    }

    default RestClientResponse put(String url, String data) throws IOException {
        return put(url, null, data);
    }

    static NameValuePair param(String name, Object value) {
        return new BasicNameValuePair(name, value == null ? null : value.toString());
    }

}

