package org.jsoftware.restclient.impl;

import org.apache.commons.io.IOUtils;
import org.jsoftware.restclient.InvalidContentException;
import org.jsoftware.restclient.PathNotFoundException;
import org.jsoftware.restclient.TestStandardRestClientResponse;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public class StandardRestClientResponseTest {
    private static final String JSON = "{\"num\":3, \"bool\":true, \"str\":\"string value\"}";
    private static final String XML = "<store><item name=\"iname\" id=\"123\">icontent</item></store>";
    private static final String HTML;

    static {
        try(InputStream in = StandardRestClientResponseTest.class.getResourceAsStream("standardRestClientResponseTest.html")) {
            HTML = IOUtils.toString(in);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Test(expected = InvalidContentException.class)
    public void testJsonInvalidContent() throws Exception {
        new TestStandardRestClientResponse("trash").json("$.num");
    }

    @Test(expected = InvalidContentException.class)
    public void testXMLInvalidContent() throws Exception {
        new TestStandardRestClientResponse("trash").xPath("/");
    }

    @Test(expected = InvalidContentException.class)
    public void testHTMLInvalidContent() throws Exception {
        new TestStandardRestClientResponse("trash").html("div");
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
        Assert.assertTrue(obj != null);
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
        Assert.assertTrue(obj != null);
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

    @Test
    public void testHTMLById() throws Exception {
        Elements elements = new TestStandardRestClientResponse(HTML).html("#mySpan");
        Assert.assertEquals(1, elements.size());
        Element element = elements.get(0);
        Assert.assertEquals("mySpanContent", element.text());
        Assert.assertEquals("attrVal", element.attr("attr-my"));
    }

    @Test
    public void testHTMLByClass() throws Exception {
        Elements elements = new TestStandardRestClientResponse(HTML).html(".myList li");
        Assert.assertEquals(2, elements.size());
    }

}