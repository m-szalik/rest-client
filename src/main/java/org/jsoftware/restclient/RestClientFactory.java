package org.jsoftware.restclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jsoftware.restclient.impl.ApacheHttpClientImplRestClient;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory for RestClient.
 * Implementation depends on available libraries.
 * <p>Currently only Apache HttpClient is supported.</p>
 * @author szalik
 */
public class RestClientFactory {
    private final Set<RestClientFeature> enabledFeatures = new HashSet<>();
    private RestClientPlugin[] plugins = new RestClientPlugin[0];
    private final RequestConfig requestConfig = RequestConfig.DEFAULT;
    private HttpClientBuilder builder;

    public RestClientFactory() {
        builder = HttpClients.custom().setMaxConnPerRoute(50).setMaxConnTotal(200).setUserAgent("org.jsoftware.restClient");
    }

    public RestClientFactory enableFeature(RestClientFeature feature) {
        enabledFeatures.add(feature);
        return this;
    }

    public RestClientFactory userAgent(String userAgent) {
        builder.setUserAgent(userAgent);
        return this;
    }

    public RestClientFactory addPlugin(RestClientPlugin plugin) {
        RestClientPlugin[] pls = new RestClientPlugin[plugins.length+1];
        System.arraycopy(plugins, 0, pls, 0, plugins.length);
        pls[plugins.length] = plugin;
        plugins = pls;
        return this;
    }

    public RestClientFactory timeout(long millis) {
        if (millis > 0) {
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            requestConfigBuilder.setConnectTimeout((int) millis);
            requestConfigBuilder.setSocketTimeout((int) millis);
            requestConfigBuilder.setConnectionRequestTimeout((int) millis);
            builder.setDefaultRequestConfig(requestConfigBuilder.build());
        } else {
            builder.setDefaultRequestConfig(RequestConfig.DEFAULT);
        }
        return this;
    }

    public RestClient newRestClient() {
        RestClientFeature[] features = enabledFeatures.toArray(new RestClientFeature[enabledFeatures.size()]);
        return new ApacheHttpClientImplRestClient(features, plugins, builder);
    }
}
