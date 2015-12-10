package org.jsoftware.rest;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * @author szalik
 */
public interface RestClientPlugin {

    interface PluginContext {
        HttpRequestBase getRequest();
        RestClientResponse getResponse();
        void setRequest(HttpRequestBase request);
        void setResponse(RestClientResponse response);
    }

    interface PluginChain {
        void continueChain() throws Exception;
    }

    void plugin(PluginContext context, PluginChain chain) throws Exception;
}
