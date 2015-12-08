package org.jsoftware.rest;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * @author szalik
 */
public interface HttpClientPlugin {

    interface PluginContext {
        HttpRequestBase getRequest();
        ClientResponse getResponse();
        void setRequest(HttpRequestBase request);
        void setResponse(ClientResponse response);
    }

    interface PluginChain {
        void continueChain() throws Exception;
    }

    void plugin(PluginContext context, PluginChain chain) throws Exception;
}
