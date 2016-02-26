package org.jsoftware.restclient;

import org.jsoftware.restclient.impl.ApacheHttpClientImplRestClient;
import org.jsoftware.utils.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory for RestClient.
 * Implementation depends on available libraries.
 * @author szalik
 */
public class RestClientFactory {
    private final boolean useApacheHttpClient;
    private final Set<RestClientFeature> enabledFeatures = new HashSet<>();

    public RestClientFactory() {
        boolean b;
        try {
            Class.forName("org.apache.http.client.HttpClient");
            b = true;
        } catch (ClassNotFoundException e) {
            b = false;
        }
        useApacheHttpClient = b;
    }

    public RestClientFactory enableFeature(RestClientFeature feature) {
        enabledFeatures.add(feature);
        return this;
    }

    public RestClient newRestClient(RestClientPlugin... plugins) {
        RestClientFeature[] features = enabledFeatures.toArray(new RestClientFeature[enabledFeatures.size()]);
        if (useApacheHttpClient) {
            return new ApacheHttpClientImplRestClient(features, plugins);
        } else {
            // pure java client
            throw new NotImplementedException("Not yet implemented");
        }
    }
}
