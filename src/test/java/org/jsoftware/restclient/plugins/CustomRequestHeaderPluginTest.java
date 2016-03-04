package org.jsoftware.restclient.plugins;

import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 */
public class CustomRequestHeaderPluginTest extends AbstractPluginTest {
    private final CustomRequestHeaderPlugin plugin = new CustomRequestHeaderPlugin("tName", "tValue");

    @Test
    public void testAddHeader() throws Exception {
        Optional<Header> header = callAndFindHeader();
        assertTrue("Missing header", header.isPresent());
    }

    @Test
    public void testDisableEnableAddHeader() throws Exception {
        Optional<Header> header1 = callAndFindHeader();
        assertTrue("Missing header", header1.isPresent());

        plugin.disable();
        Optional<Header> header2 = callAndFindHeader();
        assertFalse("Header found when disabled", header2.isPresent());

        plugin.enable();
        Optional<Header> header3 = callAndFindHeader();
        assertTrue("Missing header", header3.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHeaderName() throws Exception {
        new CustomRequestHeaderPlugin("", "val");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHeaderValue() throws Exception {
        new CustomRequestHeaderPlugin("hName", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHeaderValueSet() throws Exception {
        plugin.setHeaderValue("");
    }

    @Test
    public void testEnableDisable() throws Exception {
        plugin.enable();
        Assert.assertTrue(plugin.isEnabled());
        plugin.disable();
        Assert.assertFalse(plugin.isEnabled());
    }

    private Optional<Header> callAndFindHeader() throws Exception {
        final HeadersHolder headersHolder = new HeadersHolder();
        call(plugin, get("http://nowhere.com"), (r) -> {
            headersHolder.headers = r.getAllHeaders();
            return stdResponse(200, "OK");
        });
        for(Header h : headersHolder.headers) {
            if ("tName".equals(h.getName()) && "tValue".equals(h.getValue())) {
                return Optional.of(h);
            }
        }
        return Optional.empty();
    }
}

class HeadersHolder {
    Header[] headers;
}