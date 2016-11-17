package org.jsoftware.restclient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 */
public class ClientEndToEndTest {
    private final static String TEST_URL = "http://jsoftware.org/wp-content/rest-client-test.php";
    private RestClient client;

    @Before
    public void setUp() throws Exception {
        RestClientFactory factory = new RestClientFactory();
        client = factory.newRestClient();
    }

    @After
    public void tearDown() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void testGet() throws Exception {
        RestClientResponse resp = client.get(TEST_URL).execute();
        assertEquals(200, resp.getStatusLine().getStatusCode());
        assertEquals("Method:GET", resp.getContent().trim());
    }

    @Test
    public void testGetWithSingleParameter() throws Exception {
        RestClientResponse resp = client.get(TEST_URL).parameter("par1", "par1 value").execute();
        assertEquals(200, resp.getStatusLine().getStatusCode());
        assertEquals("Method:GET\nGET par1=par1 value", resp.getContent().trim());
    }

    @Test
    public void testGetWithMultipleParameters() throws Exception {
        RestClientResponse resp = client.get(TEST_URL).parameter("par1", "par1 value").parameter("par", "a").parameter("par", "b").execute();
        assertEquals(200, resp.getStatusLine().getStatusCode());
    }

    @Ignore // fixme
    @Test
    public void testPostBodyText() throws Exception {
        RestClientResponse resp = client.post(TEST_URL).body("PostData", ContentType.DEFAULT_TEXT).execute();
        assertEquals("Method:POST\nRawPost: PostData", resp.getContent().trim());
    }

    @Ignore // fixme
    @Test
    public void testPostBodyBytes() throws Exception {
        byte[] body = new byte[] { 70, 74, 78, 82 };
        RestClientResponse resp = client.post(TEST_URL).body(body, ContentType.APPLICATION_OCTET_STREAM).execute();
        assertEquals("Method:POST\nRawPost: FJNR", resp.getContent().trim());
    }

    @Ignore // fixme
    @Test
    public void testPostBodyInputStream() throws Exception {
        byte[] body = new byte[] { 70, 74, 78, 82 };
        RestClientResponse resp = client.post(TEST_URL).body(new ByteArrayInputStream(body), ContentType.APPLICATION_OCTET_STREAM).execute();
        assertEquals("Method:POST\nRawPost: FJNR", resp.getContent().trim());
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
    public void testResponseDumpWithHeaders() throws Exception {
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
        assertTrue(header.get().length() > 0);
        header = resp.getHeader("WhateverNotExisting");
        assertFalse(header.isPresent());
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
            assertEquals("020d2468a38f4691892e2c8b396d9077", md5Hex);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Test
    public void testReadContentTwice() throws Exception {
        RestClientResponse resp = client.get(TEST_URL).execute();
        BinaryContent binaryContent = resp.getBinaryContent();
        InputStream ins = binaryContent.getStream();
        assertNotNull(ins);
        IOUtils.toString(ins, Charset.forName("UTF-8"));
        IOUtils.closeQuietly(ins);
    }

    @Test(expected = java.net.MalformedURLException.class)
    public void testInvalidURL() throws Exception {
        client.get("xyz9").execute();
    }

    @Ignore // fixme
    @Test
    public void testParametersEncoding() throws Exception {
        RestClientResponse resp;
        resp = client.post(TEST_URL).header("Content-Type", "text/html; charset=ISO-8859-2").parameter("p", "A\u017a\u00ebZ").execute();
        assertEquals("Method:POST\nRawPost: p=A%C5%BA%C3%ABZ", resp.getContent().trim()); // UTF-8
        resp = client.post(TEST_URL).header("Content-Type", "text/html; charset=ISO-8859-2").parametersEncoding("ISO-8859-2").parameter("p", "A\u017a\u00ebZ").execute();
        assertEquals("Method:POST\nRawPost: p=A%BC%EBZ", resp.getContent().trim()); // ISO-8859-2
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBodyNullValue() throws Exception {
        client.post(TEST_URL).body((byte[]) null, ContentType.APPLICATION_OCTET_STREAM).execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBodyNullContentType() throws Exception {
        client.post(TEST_URL).body("body", null).execute();
    }

    @Test
    public void testTimeout() throws Exception {
        RestClientFactory factory = new RestClientFactory().timeout(500);
        try(RestClient client = factory.newRestClient()) {
            client.get("http://jsoftware.org/wp-content/rest-client-test.php").execute();
            fail("Exception expected");
        } catch (IOException ex) {
            // ok
        }
    }
}
