package org.jsoftware.restclient.plugins;

import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Test;

public class AuthBasicPluginTest extends AbstractPluginTest {

    @Test
    public void testAuthBasic() throws Exception {
        final StringBuilder header = new StringBuilder();
        AuthBasicPlugin plugin = new AuthBasicPlugin("user", "pass");
        call(plugin, get("http://nowhere.com"), (r) -> {
            for(Header h : r.getAllHeaders()) {
                if ("Authorization".equalsIgnoreCase(h.getName())) {
                    header.append(h.getValue());
                }
            }
            return stdResponse(200, "OK");
        });
        Assert.assertEquals("Basic dXNlcjpwYXNz", header.toString());
    }
}