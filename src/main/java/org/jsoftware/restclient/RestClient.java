package org.jsoftware.restclient;

import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author szalik
  */
public interface RestClient extends AutoCloseable {

    void setPlugins(List<RestClientPlugin> plugins);

    List<RestClientPlugin> getPlugins();

    RestClientResponse get(String url, NameValuePair... parameters) throws IOException;

    RestClientResponse delete(String url, NameValuePair... parameters) throws IOException;

    RestClientResponse post(String url, NameValuePair... parameters) throws IOException;

    RestClientResponse put(String url, NameValuePair... parameters) throws IOException;

    RestClientResponse post(String url, InputStream dataInputStream, ContentType contentType) throws IOException;

    RestClientResponse put(String url, InputStream dataInputStream, ContentType contentType) throws IOException;

    default RestClientResponse get(String url, Map<String,Object> parameters) throws IOException {
        BasicNameValuePair[] args = parameters.entrySet().stream().filter(x -> x.getValue() != null).map(x -> new BasicNameValuePair(x.getKey(), x.getValue().toString())).toArray(size -> new BasicNameValuePair[size]);
        return get(url, args);
    }


    default RestClientResponse post(String url, Map<String,Object> parameters) throws IOException {
        BasicNameValuePair[] args = parameters.entrySet().stream().filter(x -> x.getValue() != null).map(x -> new BasicNameValuePair(x.getKey(), x.getValue().toString())).toArray(size -> new BasicNameValuePair[size]);
        return post(url, args);
    }


    default RestClientResponse put(String url, Map<String,Object> parameters) throws IOException {
        BasicNameValuePair[] args = parameters.entrySet().stream().filter(x -> x.getValue() != null).map(x -> new BasicNameValuePair(x.getKey(), x.getValue().toString())).toArray(size -> new BasicNameValuePair[size]);
        return put(url, args);
    }


    default RestClientResponse delete(String url, Map<String,Object> parameters) throws IOException {
        BasicNameValuePair[] args = parameters.entrySet().stream().filter(x -> x.getValue() != null).map(x -> new BasicNameValuePair(x.getKey(), x.getValue().toString())).toArray(size -> new BasicNameValuePair[size]);
        return delete(url, args);
    }

    static NameValuePair param(String name, Object value) {
        return new BasicNameValuePair(name, value == null ? null : value.toString());
    }


}

