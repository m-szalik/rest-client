package org.jsoftware.rest.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoftware.rest.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author szalik
 */
public class DefaultHttpClient implements HttpClient {
    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;
    private HttpClientPlugin[] plugins = new HttpClientPlugin[0];


    public DefaultHttpClient(HttpClientFeature[] features) {
        httpClient = HttpClients.createDefault();
        httpClientContext = HttpClientContext.create();
        Set<HttpClientFeature> f = new HashSet<>(Arrays.asList(features));
        if (f.contains(HttpClientFeature.ENABLE_COOKIES)) {
            httpClientContext.setCookieStore(new BasicCookieStore());
        }
    }

    public DefaultHttpClient() {
        this(new HttpClientFeature[0]);
    }

    @Override
    public final void setPlugins(HttpClientPlugin... plugins) {
        this.plugins = plugins == null ? new HttpClientPlugin[0] : plugins;
    }

    @Override
    public HttpClientPlugin[] getPlugins() {
        HttpClientPlugin[] copy = new HttpClientPlugin[plugins.length];
        System.arraycopy(plugins, 0, copy, 0, plugins.length);
        return copy;
    }

    public ClientResponse get(String url, RequestCustomizer<HttpGet> customizer, NameValuePair... parameters) throws IOException {
        return doCall(url, u-> {
            HttpGet x = new HttpGet(u + args(parameters));
            if (customizer != null) {
                customizer.customize(x);
            }
            return x;
        });
    }

    public ClientResponse post(String url, RequestCustomizer<HttpPost> customizer,NameValuePair... parameters) throws IOException {
        return doCall(url, u-> prepare(new HttpPost(u), customizer, parameters));
    }

    public ClientResponse put(String url, RequestCustomizer<HttpPut> customizer, NameValuePair... parameters) throws IOException {
        return doCall(url, u-> prepare(new HttpPut(url), customizer, parameters));
    }

    public ClientResponse delete(String url, RequestCustomizer<HttpDelete> customizer, NameValuePair... parameters) throws IOException {
        return doCall(url, u-> {
            HttpDelete x = new HttpDelete(u + args(parameters));
            if (customizer != null) {
                customizer.customize(x);
            }
            return x;
        });
    }

    @Override
    public ClientResponse post(String url, RequestCustomizer<HttpPost> customizer, String data) throws IOException {
        return doCall(url, u-> prepare(new HttpPost(u), customizer, data));
    }

    @Override
    public ClientResponse put(String url, RequestCustomizer<HttpPut> customizer, String data) throws IOException {
        return doCall(url, u-> prepare(new HttpPut(u), customizer, data));
    }

    private <M extends HttpEntityEnclosingRequestBase> M prepare(M m, RequestCustomizer<M> customizer, NameValuePair... parameters) {
        try {
            if (parameters != null && parameters.length > 0) {
                m.setEntity(new UrlEncodedFormEntity(Arrays.asList(parameters), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if (customizer != null) {
            customizer.customize(m);
        }
        return m;
    }

    private <M extends HttpEntityEnclosingRequestBase> M prepare(M m, RequestCustomizer<M> customizer, String data) {
        if (StringUtils.isNotBlank(data)) {
            m.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));
        }
        if (customizer != null) {
            customizer.customize(m);
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

    private ClientResponse doCall(String url, Function<String,HttpRequestBase> method) throws IOException {
        url = prepareURL(url);
        HttpRequestBase methodToExecute = method.apply(url);
        PluginContextImpl ctx = new PluginContextImpl();
        ctx.setRequest(methodToExecute);
        InvocationChain chain = InvocationChain.create(plugins, ctx, () -> {
            HttpResponse response = httpClient.execute(methodToExecute, httpClientContext);
            ClientResponse cr = new StandardClientResponse(response);
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

class InvocationChain implements HttpClientPlugin.PluginChain {
    final Callable operation;

    InvocationChain(Callable operation) {
        this.operation = operation;
    }

    @Override
    public void continueChain() throws Exception {
        operation.call();
    }

    public static InvocationChain create(HttpClientPlugin[] plugins, HttpClientPlugin.PluginContext ctx, Callable dispatcher) {
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

class PluginContextImpl implements HttpClientPlugin.PluginContext {
    private HttpRequestBase request;
    private ClientResponse response;

    @Override
    public HttpRequestBase getRequest() {
        return request;
    }

    @Override
    public void setRequest(HttpRequestBase request) {
        this.request = request;
    }

    @Override
    public ClientResponse getResponse() {
        if (response == null) {
            throw new IllegalStateException("Request wasn't submitted yet!");
        }
        return response;
    }

    @Override
    public void setResponse(ClientResponse response) {
        this.response = response;
    }
}