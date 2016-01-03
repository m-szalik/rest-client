package org.jsoftware.restclient.plugins;

import org.jsoftware.restclient.RestClientPlugin;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class BaseURLPluginTest extends AbstractPluginTest {
    private BaseURLPlugin plugin;

    @Before
    public void setUp() throws Exception {
        plugin = new BaseURLPlugin("http://somewhere.com/query/");
    }

    @Test
    public void testFullUrl() throws Exception {
        final String URI = "http://story.com/my/";
        final StringBuilder sb = new StringBuilder();
        call(plugin, get(URI), (r)->{
            sb.append(r.getURI());
            return stdResponse(20, "");
        });
        assertEquals(URI, sb.toString());
    }

    @Test
    public void testSuffixOnly() throws Exception {
        final StringBuilder sb = new StringBuilder();
        call(plugin, get("two"), (r)->{
            sb.append(r.getURI());
            return stdResponse(20, "");
        });
        assertEquals("http://somewhere.com/query/two", sb.toString());
    }

    @Test
    public void testSuffixOnlyCutSlash() throws Exception {
        final StringBuilder sb = new StringBuilder();
        call(plugin, get("/two"), (r)->{
            sb.append(r.getURI());
            return stdResponse(20, "");
        });
        assertEquals("http://somewhere.com/query/two", sb.toString());
    }

}