package org.jsoftware.restclient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.http.entity.ContentType;
import org.jsoftware.restclient.impl.ApacheHttpClientImplRestClient;
import org.jsoftware.restclient.plugins.VerbosePlugin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 */
public class ClientEndToEndTest {
    private final static String TEST_URL = "http://jsoftware.org/wp-content/rest-client-test.php";
    private RestClient client;

    @Before
    public void setUp() throws Exception {
        PrintStream out = new PrintStream(new NullOutputStream());
        client = new ApacheHttpClientImplRestClient(new RestClientFeature[]{}, new RestClientPlugin[] {new VerbosePlugin(true, new PrintStream[]{out}) });
    }

    @Test
    public void testGet() throws Exception {
        RestClientResponse resp = client.get(TEST_URL).execute();
        assertEquals(200, resp.getStatusLine().getStatusCode());
        assertEquals("Method:GET", resp.getContent().trim());
    }

    @Test
    public void testGetWithParameters() throws Exception {
        RestClientResponse resp = client.get(TEST_URL).parameter("par1", "par1 value").execute();
        assertEquals(200, resp.getStatusLine().getStatusCode());
        assertEquals("Method:GET\nGET par1=par1 value", resp.getContent().trim());
    }

    @Test
    public void testPost() throws Exception {
        RestClientResponse resp = client.post(TEST_URL).body("PostData", ContentType.DEFAULT_TEXT).execute();
        assertEquals("Method:POST\nRawPost: PostData", resp.getContent().trim());
    }

    @Test
    public void testPostWithParameters() throws Exception {
        RestClientResponse resp = client.post(TEST_URL).parameter("p", "p value").execute();
        assertEquals("Method:POST\nPOST p=p value", resp.getContent().trim());
    }

    @Test
    public void testDeleteWithParameters() throws Exception {
        RestClientResponse resp = client.delete(TEST_URL).parameter("p", "p value").execute();
        assertEquals("Method:DELETE\nGET p=p value", resp.getContent().trim());
    }

    @Test
    public void testPutWithParameters() throws Exception {
        RestClientResponse resp = client.put(TEST_URL).execute();
        assertEquals("Method:PUT", resp.getContent().trim());
    }

    @Test
    public void testResponseDump() throws Exception {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            RestClientResponse resp = client.get(TEST_URL).execute();
            resp.dump(false, new PrintStream(out));
            String str = new String(out.toByteArray()).trim();
            assertEquals("HTTP/1.1 200 OK\nMethod:GET", str);
        }
    }

    @Test
    public void testResponseDumpWithHeadres() throws Exception {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            RestClientResponse resp = client.get(TEST_URL).execute();
            resp.dump(true, new PrintStream(out));
            String str = new String(out.toByteArray()).trim();
            assertTrue(str.startsWith("HTTP/1.1 200 OK"));
            assertTrue(str.contains("HEADER Date:"));
            assertTrue(str.endsWith("Method:GET"));
        }
    }

    @Test
    public void testResponseDumpStdOut() throws Exception {
        final PrintStream stdout = System.out;
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            System.setOut(new PrintStream(out));
            RestClientResponse resp = client.get(TEST_URL).execute();
            resp.dump(true);
            String str = new String(out.toByteArray()).trim();
            assertTrue(str.startsWith("HTTP/1.1 200 OK"));
            assertTrue(str.contains("HEADER Date:"));
            assertTrue(str.endsWith("Method:GET"));
        } finally {
            System.setOut(stdout);
        }
    }

    @Test
    public void testFindHeader() throws Exception {
        RestClientResponse resp = client.get(TEST_URL).execute();
        Optional<String> header;
        header = resp.getHeader("Date");
        Assert.assertTrue(header.get().length() > 0);
        header = resp.getHeader("WhateverNotExisting");
        Assert.assertFalse(header.isPresent());
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
        RestClientResponse resp = client.get(TEST_URL).execute();
        BinaryContent binaryContent = resp.getBinaryContent();
        InputStream ins = binaryContent.getStream();
        IOUtils.toString(ins);
        IOUtils.closeQuietly(ins);
    }


}
