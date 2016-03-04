package org.jsoftware.restclient;

import org.junit.Assert;
import org.junit.Test;

public class RestClientFactoryTest {

    @Test
    public void testCreateNewInstance() throws Exception {
        RestClientFactory factory = new RestClientFactory();
        factory.enableFeature(RestClientFeature.ENABLE_COOKIES);
        RestClient restClient = factory.newRestClient();
        Assert.assertNotNull(restClient);
    }
}