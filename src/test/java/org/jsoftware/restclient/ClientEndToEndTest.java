package org.jsoftware.restclient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.jsoftware.restclient.impl.DefaultRestClient;
import org.jsoftware.restclient.plugins.VerbosePlugin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
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

    @Test
    public void testDownloadBinaryData() throws Exception {
        RestClientResponse resp = client.get("http://jsoftware.org/wp-content/themes/twentyeleven/images/headers/chessboard.jpg").execute();
        InputStream in = resp.getBinaryContent().getStream();
        try {
            byte[] buff = IOUtils.toByteArray(in);
            String md5Hex = DigestUtils.md5Hex(buff);
            Assert.assertEquals("020d2468a38f4691892e2c8b396d9077", md5Hex);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Test
    public void testReadContentTwice() throws Exception {
        RestClientResponse resp = client.get("http://jsoftware.org").execute();
        BinaryContent binaryContent = resp.getBinaryContent();
        InputStream ins = binaryContent.getStream();
        IOUtils.toString(ins);
        IOUtils.closeQuietly(ins);
    }
}
