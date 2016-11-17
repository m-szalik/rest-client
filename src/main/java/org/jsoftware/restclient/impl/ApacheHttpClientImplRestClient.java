package org.jsoftware.restclient.impl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoftware.restclient.BaseRestClientCall;
import org.jsoftware.restclient.RestClient;
import org.jsoftware.restclient.RestClientCall;
import org.jsoftware.restclient.RestClientDataCall;
import org.jsoftware.restclient.RestClientFeature;
import org.jsoftware.restclient.RestClientPlugin;
import org.jsoftware.restclient.RestClientResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RestClient implementation based on Apache http-client
 * @author szalik
 */
public class ApacheHttpClientImplRestClient implements RestClient {
    private final CloseableHttpClient httpClient;
    private final HttpClientContext httpClientContext;
    private RestClientPlugin[] plugins = new RestClientPlugin[0];


    private static class NoneRedirectStrategy implements RedirectStrategy {
        @Override
        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            return false;
        }
        @Override
        public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            throw new AssertionError("Should be never called!");
        }
    }

    /**
     * @param features features to enable
     * @param plugins plugins to be added
     */
    public ApacheHttpClientImplRestClient(RestClientFeature[] features, RestClientPlugin[] plugins, HttpClientBuilder builder) {
        httpClientContext = HttpClientContext.create();
        Set<RestClientFeature> f = new HashSet<>(Arrays.asList(features));
        if (f.contains(RestClientFeature.ENABLE_COOKIES)) {
            httpClientContext.setCookieStore(new BasicCookieStore());
        }
        if (! f.contains(RestClientFeature.AUTOMATIC_REDIRECTS)) { // fixme - this makes modification in factory property "builder"
            builder.setRedirectStrategy(new NoneRedirectStrategy());
        }
        if (plugins != null) {
            this.plugins = plugins;
        }
        httpClient = builder.build();
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
            throw new AssertionError(e);
        }
    }



    abstract class AbstractRestClientCall<C extends BaseRestClientCall,M extends HttpRequestBase> implements BaseRestClientCall<C> {
        private final Map<String,String[]> parameters = new LinkedHashMap<>();
        protected final M method;
        private final String uri;

        protected AbstractRestClientCall(String uri, M method) {
            this.method = method;
            this.uri = uri;
        }


        @Override
        public RestClientResponse execute() throws IOException {
            PluginContextImpl ctx = new PluginContextImpl();
            ctx.setRequest(method);
            ctx.setURI(uri);
            if (! parameters.isEmpty()) {
                applyParameters(method, ctx, parameters);
            }
            InvocationChain chain = InvocationChain.create(plugins, ctx, () -> {
                try {
                    this.method.setURI(new URL(ctx.getURI()).toURI());
                } catch (URISyntaxException e) {
                    MalformedURLException mex = new MalformedURLException("Invalid url '" + uri + "'");
                    mex.initCause(e);
                    throw mex;
                }
                HttpResponse response = httpClient.execute(method, httpClientContext);
                RestClientResponse cr = new StandardRestClientResponse(response);
                ctx.setResponse(cr);
                return ctx;
            });
            try {
                chain.operation.call();
            } catch (IOException ioe) {
                throw ioe;
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

        protected abstract void applyParameters(M method, RestClientPlugin.PluginContext ctx, Map<String, String[]> params);

    }


    class RestClientCallImpl<M extends HttpRequestBase> extends ApacheHttpClientImplRestClient.AbstractRestClientCall<RestClientCall,M> implements RestClientCall {
        RestClientCallImpl(String url, M method) throws MalformedURLException {
            super(url, method);
        }

        @Override
        protected void applyParameters(M method, RestClientPlugin.PluginContext ctx, Map<String, String[]> params) {
            String uri = ctx.getURI();
            boolean hasParam = uri.contains("?");
            StringBuilder sb = new StringBuilder(uri);
            for(Map.Entry<String,String[]> x : params.entrySet()) {
                final String name = encode(x.getKey());
                for(String val : x.getValue()) {
                    sb.append(hasParam ? '&' : '?').append(name).append('=').append(encode(val));
                    hasParam = true;
                }
            }
            ctx.setURI(sb.toString());
        }
    }


    class RestClientDataCallImpl<M extends HttpEntityEnclosingRequestBase> extends ApacheHttpClientImplRestClient.AbstractRestClientCall<RestClientDataCall,M> implements RestClientDataCall {
        private Charset charset;
        RestClientDataCallImpl(String url, M method) throws MalformedURLException {
            super(url, method);
            charset = Charset.forName("UTF-8");
        }

        @Override
        protected void applyParameters(M method, RestClientPlugin.PluginContext ctx, Map<String, String[]> params) {
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
            notNullCheck(inputStream, contentType);
            method.setEntity(new InputStreamEntity(inputStream, contentType));
            return this;
        }

        @Override
        public RestClientDataCall body(byte[] data, ContentType contentType) {
            notNullCheck(data, contentType);
            method.setEntity(new ByteArrayEntity(data, contentType));
            return this;
        }

        @Override
        public RestClientDataCall body(String data, ContentType contentType) {
            notNullCheck(data, contentType);
            method.setEntity(new StringEntity(data, contentType));
            return this;
        }

        @Override
        public RestClientDataCall parametersEncoding(String charset) {
            this.charset = Charset.forName(charset);
            return this;
        }

        private void notNullCheck(Object data, ContentType ct) {
            if (data == null) {
                throw new IllegalArgumentException("Body data cannot be null.");
            }
            if (ct == null) {
                throw new IllegalArgumentException("Body content-type must be set.");
            }
        }
    }
}

