package org.jsoftware.restclient;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * RestClientPlugin allows modification of http requests and responses
 * @author szalik
 */
public interface RestClientPlugin {

    /**
     * Plugin context
     * @author szalik
     */
    interface PluginContext {
        /**
         * @return current http request
         */
        HttpRequestBase getRequest();

        /**
         * Not available before request is not sent
         * @throws IllegalStateException if response is not available
         * @return current http response
         */
        RestClientResponse getResponse();

        /**
         * Override http request
         * @param request set request
         */
        void setRequest(HttpRequestBase request);

        /**
         * Override http response
         * @param response set client's response
         */
        void setResponse(RestClientResponse response);
    }


    /**
     * Chain of responsibility for RestClientPlugins
     */
    interface PluginChain {
        /**
         * Continue chain of plugins
         * @throws Exception if any error in chain occurred
         */
        void continueChain() throws Exception;
    }

    /**
     * Plugin main method
     * @param context http request context
     * @param chain chain of plugins
     * @throws Exception if any error in chain occurred
     */
    void plugin(PluginContext context, PluginChain chain) throws Exception;
}
