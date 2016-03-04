package org.jsoftware.restclient.plugins;

import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Test;

public class AuthBearerPluginTest extends AbstractPluginTest {

    @Test
    public void testAuthBearer() throws Exception {
        AuthBearerPlugin plugin = new AuthBearerPlugin("token");
        Assert.assertEquals("Bearer token", call(plugin));

        plugin.clearToken();
        Assert.assertEquals("", call(plugin));

        plugin.setToken("Token2");
        Assert.assertEquals("Bearer Token2", call(plugin));
    }


    @Test
    public void testAuthBearerNoArgConstructor() throws Exception {
        AuthBearerPlugin plugin = new AuthBearerPlugin();
        Assert.assertEquals("", call(plugin));
    }


    private String call(AuthBearerPlugin plugin) throws Exception {
        final StringBuilder header = new StringBuilder();
        call(plugin, get("http://nowhere.com"), (r) -> {
            for (Header h : r.getAllHeaders()) {
                if ("Authorization".equalsIgnoreCase(h.getName())) {
                    header.append(h.getValue());
                }
            }
            return stdResponse(200, "OK");
        });
        return header.toString();
    }

}