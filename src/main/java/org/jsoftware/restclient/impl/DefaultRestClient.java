package org.jsoftware.restclient.impl;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoftware.restclient.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Deafult implementation of RestClient
 * @author szalik
 */
public class DefaultRestClient implements RestClient {
    private final CloseableHttpClient httpClient;
    private final HttpClientContext httpClientContext;
    private RestClientPlugin[] plugins = new RestClientPlugin[0];


    /**
     * Instance with features enabled
     * @param features features to enable
     */
    public DefaultRestClient(RestClientFeature[] features) {
        this(features, new RestClientPlugin[0]);
    }

    /**
     * No special features is enabled.
     * @param plugins plugins to enable
     * @see #setPlugins(List)
     */
    public DefaultRestClient(RestClientPlugin... plugins) {
        this(new RestClientFeature[0], plugins);
    }

    /**
     * @param features features to enable
     * @param plugins plugins to be added
     * @see #setPlugins(List)
     */
    public DefaultRestClient(RestClientFeature[] features, RestClientPlugin... plugins) {
        httpClient = HttpClients.custom().setMaxConnPerRoute(50).setMaxConnTotal(200).setUserAgent("org.jsoftware.restClient").build();
        httpClientContext = HttpClientContext.create();
        Set<RestClientFeature> f = new HashSet<>(Arrays.asList(features));
        if (f.contains(RestClientFeature.ENABLE_COOKIES)) {
            httpClientContext.setCookieStore(new BasicCookieStore());
        }
        if (plugins != null) {
            this.plugins = plugins;
        }
    }

    /**
     * Instance with no features enabled and no plugins
     */
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

    @Override
    public RestClientCall get(String url) throws MalformedURLException {
        return new RestClientCallImpl<>(url, new HttpGet());
    }

    @Override
    public RestClientCall delete(String url) throws MalformedURLException {
        return new RestClientCallImpl<>(url, new HttpDelete());
    }

    @Override
    public RestClientCall head(String url) throws MalformedURLException {
        return new RestClientCallImpl<>(url, new HttpHead());
    }

    @Override
    public RestClientCall options(String url) throws MalformedURLException {
        return new RestClientCallImpl<>(url, new HttpOptions());
    }

    @Override
    public RestClientDataCall post(String url) throws MalformedURLException {
        return new RestClientDataCallImpl<>(url, new HttpPost());
    }

    @Override
    public RestClientDataCall put(String url) throws MalformedURLException {
        return new RestClientDataCallImpl<>(url, new HttpPut());
    }

    @Override
    public void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }


    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }



    abstract class AbstractRestClientCall<C extends BaseRestClientCall,M extends HttpRequestBase> implements BaseRestClientCall<C> {
        private final Map<String,String[]> parameters = new LinkedHashMap<>();
        protected final M method;

        protected AbstractRestClientCall(String url, M method) throws MalformedURLException {
            this.method = method;
            try {
                this.method.setURI(new URL(url).toURI());
            } catch (URISyntaxException e) {
                MalformedURLException mex = new MalformedURLException("Invalid url '" + url + "'");
                mex.initCause(e);
                throw mex;
            }
        }

        @Override
        public RestClientResponse execute() throws IOException {
            if (! parameters.isEmpty()) {
                applyParameters(method, parameters);
            }
            PluginContextImpl ctx = new PluginContextImpl();
            ctx.setRequest(method);
            InvocationChain chain = InvocationChain.create(plugins, ctx, () -> {
                HttpResponse response = httpClient.execute(method, httpClientContext);
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
        public C parameter(String name, Object value) {
            String[] actual = parameters.get(name);
            String str = value == null ? "" : value.toString();
            if (actual == null) {
                parameters.put(name, new String[] { str });
            } else {
                String[] values = new String[actual.length + 1];
                System.arraycopy(actual, 0, values, 0, actual.length);
                values[actual.length] = str;
                parameters.put(name, values);
            }
            return (C) this;
        }

        @Override
        public C header(String name, String value) {
            method.setHeader(name, value);
            return (C) this;
        }

        protected abstract void applyParameters(M method, Map<String,String[]> params);
    }


    class RestClientCallImpl<M extends HttpRequestBase> extends DefaultRestClient.AbstractRestClientCall<RestClientCall,M> implements RestClientCall {
        RestClientCallImpl(String url, M method) throws MalformedURLException {
            super(url, method);
        }

        @Override
        protected void applyParameters(M method, Map<String,String[]> params) {
            String uri = method.getURI().toASCIIString();
            boolean hasParam = uri.contains("?");
            StringBuilder sb = new StringBuilder(method.getURI().toASCIIString());
            for(Map.Entry<String,String[]> x : params.entrySet()) {
                final String name = encode(x.getKey());
                for(String val : x.getValue()) {
                    sb.append(hasParam ? '&' : '?').append(name).append('=').append(encode(val));
                    hasParam = true;
                }
            }
            try {
                method.setURI(new URI(sb.toString()));
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Invalid uri '" + sb + "'", e);
            }
        }
    }


    class RestClientDataCallImpl<M extends HttpEntityEnclosingRequestBase> extends DefaultRestClient.AbstractRestClientCall<RestClientDataCall,M> implements RestClientDataCall {
        private Charset charset;
        RestClientDataCallImpl(String url, M method) throws MalformedURLException {
            super(url, method);
            charset = Charset.forName("UTF-8");
        }

        @Override
        protected void applyParameters(M method, Map<String, String[]> params) {
            List<NameValuePair> list = new LinkedList<>();
            for(Map.Entry<String,String[]> x : params.entrySet()) {
                final String name = x.getKey();
                for(String val : x.getValue()) {
                    list.add(new BasicNameValuePair(name, val));
                }
            }
            method.setEntity(new UrlEncodedFormEntity(list, charset));
        }

        @Override
        public RestClientDataCall body(InputStream inputStream, ContentType contentType) {
            method.setEntity(new InputStreamEntity(inputStream, contentType));
            return this;
        }

        @Override
        public RestClientDataCall body(byte[] data, ContentType contentType) {
            method.setEntity(new ByteArrayEntity(data, contentType));
            return this;
        }

        @Override
        public RestClientDataCall body(String data, ContentType contentType) {
            method.setEntity(new StringEntity(data, contentType));
            return this;
        }

        @Override
        public RestClientDataCall parametersEncoding(String charset) {
            this.charset = Charset.forName(charset);
            return this;
        }
    }
}

