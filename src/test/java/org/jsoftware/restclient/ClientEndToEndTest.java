package org.jsoftware.restclient;

import org.jsoftware.restclient.impl.DefaultRestClient;
import org.jsoftware.restclient.plugins.VerbosePlugin;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 */
public class ClientEndToEndTest {
    private RestClient client;

    @Before
    public void setUp() throws Exception {
        client = new DefaultRestClient(new VerbosePlugin(true, new PrintStream[]{}));
    }

    @Test
    public void testJSoftware() throws Exception {
        RestClientResponse resp = client.get("http://jsoftware.org").execute();
        assertEquals(200, resp.getStatusLine().getStatusCode());
    }

    @Test(expected = UnknownHostException.class)
    public void testConnectionRefused() throws Exception {
        client.get("http://nandueb-e8y34.com.xa.nowhere").execute();
    }
}
