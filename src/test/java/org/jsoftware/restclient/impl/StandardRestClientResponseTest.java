package org.jsoftware.restclient.impl;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.jsoftware.restclient.InvalidContentException;
import org.jsoftware.restclient.PathNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.xpath.XPathConstants;
import java.io.IOException;

/**
 */
public class StandardRestClientResponseTest {
    private static final String JSON = "{\"num\":3, \"bool\":true, \"str\":\"string value\"}";
    private static final String XML = "<store><item name=\"iname\" id=\"123\">icontent</item></store>";

    @Test(expected = InvalidContentException.class)
    public void testJsonInvalidContent() throws Exception {
        new TestStandardRestClientResponse("trash").json("$.num");
    }

    @Test(expected = InvalidContentException.class)
    public void testXMLInvalidContent() throws Exception {
        new TestStandardRestClientResponse("trash").xPath("/");
    }


    @Test
    public void testJsonNumber() throws Exception {
        Object obj = new TestStandardRestClientResponse(JSON).json("$.num");
        Assert.assertTrue(obj instanceof Number);
        Assert.assertEquals(3, ((Number) obj).intValue());
    }

    @Test
    public void testJsonString() throws Exception {
        Object obj = new TestStandardRestClientResponse(JSON).json("$.str");
        Assert.assertTrue(obj instanceof String);
        Assert.assertEquals("string value", obj);
    }

    @Test
    public void testJsonBool() throws Exception {
        Object obj = new TestStandardRestClientResponse(JSON).json("$.bool");
        Assert.assertTrue(obj instanceof Boolean);
        Assert.assertEquals(Boolean.TRUE, obj);
    }

    @Test(expected = PathNotFoundException.class)
    public void testJsonNotExistingField() throws Exception {
        new TestStandardRestClientResponse(JSON).json("$.x");
    }

    @Test
    public void testXMLAttribute() throws Exception {
        Object obj = new TestStandardRestClientResponse(XML).xPath("/store/item/@name");
        Assert.assertTrue(obj instanceof String);
        Assert.assertEquals("iname", obj);
    }

    @Test
    public void testXMLAttributeAsNumber() throws Exception {
        Object obj = new TestStandardRestClientResponse(XML).xPath("/store/item/@id", XPathConstants.NUMBER);
        Assert.assertTrue(obj instanceof Number);
        Assert.assertEquals(123, ((Number)obj).intValue());
    }

    @Test
    public void testXMLTextContent() throws Exception {
        Object obj = new TestStandardRestClientResponse(XML).xPath("/store/item");
        Assert.assertTrue(obj instanceof String);
        Assert.assertEquals("icontent", obj);
    }

    @Test(expected = PathNotFoundException.class)
    public void testXMLNotExistingTag() throws Exception {
        new TestStandardRestClientResponse(XML).xPath("/store/location");
    }

    @Test(expected = PathNotFoundException.class)
    public void testXMLNotExistingAttribute() throws Exception {
        new TestStandardRestClientResponse(XML).xPath("/store/item/@notexisting", XPathConstants.NUMBER);
    }



    class TestStandardRestClientResponse extends AbstractStandardRestClientResponse {
        private final String content;

        TestStandardRestClientResponse(String content) {this.content = content;}

        @Override
        public StatusLine getStatusLine() {
            throw new AssertionError("Not implemented");
        }

        @Override
        public Header[] getAllHeaders() {
            return new Header[0];
        }

        @Override
        public String getContent() throws IOException {
            return content;
        }
    }
}