package org.jsoftware.restclient.impl;

import org.apache.http.client.methods.HttpRequestBase;
import org.jsoftware.restclient.RestClientPlugin;
import org.jsoftware.restclient.RestClientResponse;

import java.util.concurrent.Callable;

/**
 * Chain of responsibility
 * @author szalik
 */
class InvocationChain implements RestClientPlugin.PluginChain {
    final Callable operation; // package access level because of tests

    private InvocationChain(Callable operation) {
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
    private String uri;

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
    public boolean isResponseAvailable() {
        return response != null;
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public void setURI(String uri) {
        this.uri = uri;
    }

    @Override
    public void setResponse(RestClientResponse response) {
        this.response = response;
    }
}