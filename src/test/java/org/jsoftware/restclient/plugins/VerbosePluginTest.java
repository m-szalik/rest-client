package org.jsoftware.restclient.plugins;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.jsoftware.restclient.TestStandardRestClientResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class VerbosePluginTest extends AbstractPluginTest {
    private VerbosePlugin plugin;
    private ByteArrayOutputStream out;

    @Before
    public void setUp() throws Exception {
        out = new ByteArrayOutputStream();
        plugin = new VerbosePlugin(true, new PrintStream[] { new PrintStream(out) },
                VerbosePlugin.RenderingOption.REQUEST_URL, VerbosePlugin.RenderingOption.REQUEST_HEADERS,
                VerbosePlugin.RenderingOption.REQUEST_BODY, VerbosePlugin.RenderingOption.RESPONSE_STATUS,
                VerbosePlugin.RenderingOption.RESPONSE_HEADERS, VerbosePlugin.RenderingOption.RESPONSE_BODY);
    }

    @After
    public void tearDown() throws Exception {
        out.close();
    }

    @Test
    public void testPost200() throws Exception {
        HttpPost post = new HttpPost("http://request.nowhere");
        post.setEntity(new StringEntity("post-data"));
        call(plugin, post, (r) -> stdResponse(200, "Response=OK"));
        String str = new String(out.toByteArray()).replace('\n', ' ').trim();
        Assert.assertEquals("> POST http://request.nowhere > post-data < HTTP/1.1 200 Code 200 < Response=OK", str);
    }

    @Test
    public void testGet200() throws Exception {
        HttpGet get = new HttpGet("http://request.nowhere?param=value");
        get.addHeader("x-header", "x-header-value");
        call(plugin, get, (r) -> stdResponse(200, "Response=OK"));
        String str = new String(out.toByteArray()).replace('\n', ' ').trim();
        Assert.assertEquals("> GET http://request.nowhere?param=value < (header) x-header: x-header-value < HTTP/1.1 200 Code 200 < Response=OK", str);
    }

    @Test
    public void testGet404() throws Exception {
        HttpGet get = new HttpGet("http://request.nowhere?param=value");
        call(plugin, get, (r) -> stdResponse(404, "Response=404"));
        String str = new String(out.toByteArray()).replace('\n', ' ').trim();
        Assert.assertEquals("> GET http://request.nowhere?param=value < HTTP/1.1 404 Code 404 < Response=404", str);
    }

    @Test
    public void testGet404WithDefaultConstructor() throws Exception {
        VerbosePlugin localPlugin = new VerbosePlugin(false, new PrintStream[] { new PrintStream(out) });
        HttpGet get = new HttpGet("http://request.nowhere?param=value");
        call(localPlugin, get, (r) -> stdResponse(404, "Response=404"));
        String str = new String(out.toByteArray()).replace('\n', ' ').trim();
        Assert.assertEquals("> GET http://request.nowhere?param=value < HTTP/1.1 404 Code 404 < Response=404", str);
    }

    @Test
    public void testUnhandledException() throws Exception {
        VerbosePlugin localPlugin = new VerbosePlugin(false, new PrintStream[] { new PrintStream(out) });
        HttpGet get = new HttpGet("http://request.nowhere");
        try {
            call(localPlugin, get, (r) -> {
                throw new IllegalStateException("unhandled exception");
            });
            Assert.fail("Exception expected");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("unhandled exception", ex.getMessage());
        }
        String str = new String(out.toByteArray()).replace('\n', ' ').trim();
        Assert.assertEquals("> GET http://request.nowhere * java.lang.IllegalStateException: unhandled exception", str);
    }

    @Test
    public void testContentRepeatable() throws Exception {
        VerbosePlugin localPlugin = new VerbosePlugin(false, new PrintStream[] { new PrintStream(out) });
        HttpGet get = new HttpGet("http://request.nowhere");
        call(localPlugin, get, (r) -> {
            BasicStatusLine sl = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "Code 200");
            return new TestStandardRestClientResponse(sl, new byte[] {67,70,77,78,79,92,91}, true);
        });
        String str = new String(out.toByteArray()).replace('\n', ' ').trim();
        Assert.assertTrue(str.endsWith(" < CFMNO\\["));
    }

    @Test
    public void testContentNotRepeatable() throws Exception {
        VerbosePlugin localPlugin = new VerbosePlugin(false, new PrintStream[] { new PrintStream(out) });
        HttpGet get = new HttpGet("http://request.nowhere");
        call(localPlugin, get, (r) -> {
            BasicStatusLine sl = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "Code 200");
            return new TestStandardRestClientResponse(sl, new byte[] {67,70,77,78,79,92,91}, false);
        });
        String str = new String(out.toByteArray()).replace('\n', ' ').trim();
        Assert.assertFalse(str.contains("CFMNO\\["));
    }



}