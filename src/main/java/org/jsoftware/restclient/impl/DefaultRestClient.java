package org.jsoftware.restclient.impl;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoftware.restclient.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author szalik
 */
public class DefaultRestClient implements RestClient {
    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;
    private RestClientPlugin[] plugins = new RestClientPlugin[0];


    public DefaultRestClient(RestClientFeature[] features) {
        this(features, new RestClientPlugin[0]);
    }

    public DefaultRestClient(RestClientFeature[] features, RestClientPlugin... plugins) {
        httpClient = HttpClients.createDefault();
        httpClientContext = HttpClientContext.create();
        Set<RestClientFeature> f = new HashSet<>(Arrays.asList(features));
        if (f.contains(RestClientFeature.ENABLE_COOKIES)) {
            httpClientContext.setCookieStore(new BasicCookieStore());
        }
    }

    public DefaultRestClient() {
        this(new RestClientFeature[0]);
    }

    @Override
    public final void setPlugins(List<RestClientPlugin> plugins) {
        this.plugins = plugins == null ? new RestClientPlugin[0] : plugins.toArray(new RestClientPlugin[plugins.size()]);
    }

    @Override
    public List<RestClientPlugin> getPlugins() {
        List<RestClientPlugin> list = new LinkedList<>();
        for(RestClientPlugin plugin : plugins) {
            list.add(plugin);
        }
        return Collections.unmodifiableList(list);
    }

    public RestClientResponse get(String url, NameValuePair... parameters) throws IOException {
        return doCall(url, u-> {
            HttpGet x = new HttpGet(u + args(parameters));
            return x;
        });
    }

    public RestClientResponse post(String url, NameValuePair... parameters) throws IOException {
        return doCall(url, u-> prepare(new HttpPost(u), parameters));
    }

    public RestClientResponse put(String url, NameValuePair... parameters) throws IOException {
        return doCall(url, u-> prepare(new HttpPut(url), parameters));
    }

    public RestClientResponse delete(String url, NameValuePair... parameters) throws IOException {
        return doCall(url, u-> {
            HttpDelete x = new HttpDelete(u + args(parameters));
            return x;
        });
    }

    @Override
    public RestClientResponse post(String url, InputStream is, ContentType contentType) throws IOException {
        return doCall(url, u-> prepare(new HttpPost(u), is, contentType));
    }

    @Override
    public RestClientResponse put(String url, InputStream is, ContentType contentType) throws IOException {
        return doCall(url, u-> prepare(new HttpPut(u), is, contentType));
    }

    private <M extends HttpEntityEnclosingRequestBase> M prepare(M m, NameValuePair... parameters) {
        try {
            if (parameters != null && parameters.length > 0) {
                m.setEntity(new UrlEncodedFormEntity(Arrays.asList(parameters), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return m;
    }

    private <M extends HttpEntityEnclosingRequestBase> M prepare(M m, InputStream dataInputStream, ContentType contentType) {
        if (dataInputStream != null) {
            m.setEntity(new InputStreamEntity(dataInputStream, contentType));
        }
        return m;
    }



    private String args(NameValuePair... parameters) {
        StringBuilder args = new StringBuilder();
        if (parameters != null && parameters.length > 0) {
            args.append("?");
            for(NameValuePair nvp : parameters) {
                args.append(encode(nvp.getName())).append('=').append(encode(nvp.getValue())).append('&');
            }
        }
        String s = args.toString();
        if (s.length() > 0) {
            s = s.substring(0, s.length() -1);
        }
        return s;
    }

    private RestClientResponse doCall(String url, Function<String,HttpRequestBase> method) throws IOException {
        url = prepareURL(url);
        HttpRequestBase methodToExecute = method.apply(url);
        PluginContextImpl ctx = new PluginContextImpl();
        ctx.setRequest(methodToExecute);
        InvocationChain chain = InvocationChain.create(plugins, ctx, () -> {
            HttpResponse response = httpClient.execute(methodToExecute, httpClientContext);
            RestClientResponse cr = new StandardRestClientResponse(response);
            ctx.setResponse(cr);
            return ctx;
        });
        try {
            chain.operation.call();
        } catch (Exception e) {
            throw new IOException("Chain exception", e);
        }
        return ctx.getResponse();
    }

    @Override
    public void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    protected String prepareURL(String urlArgument) {
        return urlArgument;
    }


    private String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}

class InvocationChain implements RestClientPlugin.PluginChain {
    final Callable operation;

    InvocationChain(Callable operation) {
        this.operation = operation;
    }

    @Override
    public void continueChain() throws Exception {
        operation.call();
    }

    public static InvocationChain create(RestClientPlugin[] plugins, RestClientPlugin.PluginContext ctx, Callable dispatcher) {
        InvocationChain last = new InvocationChain(dispatcher);
        for(int i=plugins.length -1; i>=0; i--) {
            final int j = i;
            final InvocationChain next = last;
            last = new InvocationChain(()->{
                plugins[j].plugin(ctx, next);
                return ctx;
            });
        }
        return last;
    }
}

class PluginContextImpl implements RestClientPlugin.PluginContext {
    private HttpRequestBase request;
    private RestClientResponse response;

    @Override
    public HttpRequestBase getRequest() {
        return request;
    }

    @Override
    public void setRequest(HttpRequestBase request) {
        this.request = request;
    }

    @Override
    public RestClientResponse getResponse() {
        if (response == null) {
            throw new IllegalStateException("Request wasn't submitted yet!");
        }
        return response;
    }

    @Override
    public void setResponse(RestClientResponse response) {
        this.response = response;
    }
}