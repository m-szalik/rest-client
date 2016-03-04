package org.jsoftware.restclient.plugins;

import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Test;

public class AuthBearerPluginTest extends AbstractPluginTest {

    @Test
    public void testAuthBearer() throws Exception {
        final StringBuilder header = new StringBuilder();
        AuthBearerPlugin plugin = new AuthBearerPlugin("token");
        call(plugin, get("http://nowhere.com"), (r) -> {
            for(Header h : r.getAllHeaders()) {
                if ("Authorization".equalsIgnoreCase(h.getName())) {
                    header.append(h.getValue());
                }
            }
            return stdResponse(200, "OK");
        });
        Assert.assertEquals("Bearer token", header.toString());
    }
}