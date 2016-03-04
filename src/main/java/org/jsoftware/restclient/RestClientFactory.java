package org.jsoftware.restclient;

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

    public RestClientFactory enableFeature(RestClientFeature feature) {
        enabledFeatures.add(feature);
        return this;
    }

    public RestClient newRestClient(RestClientPlugin... plugins) {
        RestClientFeature[] features = enabledFeatures.toArray(new RestClientFeature[enabledFeatures.size()]);
        return new ApacheHttpClientImplRestClient(features, plugins);
    }
}
