package org.jsoftware.restclient.impl;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.jsoftware.restclient.InvalidContentException;
import org.jsoftware.restclient.PathNotFoundException;
import org.jsoftware.restclient.RestClientResponse;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

/**
 *
 * @author szalik
 */
public abstract class AbstractStandardRestClientResponse implements RestClientResponse {
    private DocumentContext json;
    private Document xmlDocument;
    private org.jsoup.nodes.Document htmlDocument;


    @Override
    public synchronized Object json(String path) throws IOException, PathNotFoundException {
        if (json == null) {
            String content = getContent();
            if (! StringUtils.isBlank(content)) {
                json = JsonPath.parse(content);
            }
            if (json == null || json.json() instanceof CharSequence) {
                throw new InvalidContentException("Content is not valid JSON document.", getContent(), StringUtils.isBlank(content) ? new BlankContentException() : null);
            }
        }
        try {
            return json.read(path);
        } catch (com.jayway.jsonpath.PathNotFoundException ex) {
            throw new PathNotFoundException(path, ex);
        }
    }


    private synchronized XPathExpression xPathInternal(String xPath) throws ParserConfigurationException, IOException, XPathExpressionException, PathNotFoundException {
        if (xmlDocument == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            String content = getContent();
            try {
                checkNotBlank(content);
                xmlDocument = builder.parse(new InputSource(new StringReader(content)));
            } catch (SAXException|BlankContentException e) {
                throw new InvalidContentException("Content is not valid XML document.", getContent(), e);
            }
        }
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile(xPath);
        Node node = (Node) expr.evaluate(xmlDocument, XPathConstants.NODE);
        if (node == null) {
            throw new PathNotFoundException(xPath, null);
        }
        return expr;
    }



    @Override
    public Object xPath(String xPath, QName type) throws IOException, XPathExpressionException, PathNotFoundException {
        XPathExpression expr;
        try {
            expr = xPathInternal(xPath);
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
        return expr.evaluate(xmlDocument, type);
    }

    @Override
    public String xPath(String xPath) throws IOException, XPathExpressionException, PathNotFoundException {
        XPathExpression expr;
        try {
            expr = xPathInternal(xPath);
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
        return expr.evaluate(xmlDocument);
    }

    @Override
    public synchronized Elements html(String jQueryExpression) throws IOException {
        if (htmlDocument == null) {
            String content = getContent();
            if (StringUtils.isBlank(content) || ! StringUtils.containsIgnoreCase(content, "<html")) {
                throw new InvalidContentException("Content is not valid HTML document.", content, StringUtils.isBlank(content) ? new BlankContentException() : null);
            }
            htmlDocument = Jsoup.parse(content);
        }
        return htmlDocument.select(jQueryExpression);
    }

    @Override
    public void dump(boolean withHeaders, PrintStream to) throws IOException {
        StringBuilder s = new StringBuilder();
        s.append(getStatusLine()).append('\n');
        if (withHeaders) {
            for(Header h : getAllHeaders()) {
                s.append("HEADER ").append(h.getName()).append(":").append(h.getValue()).append('\n');
            }
        }
        s.append(getContent());
        to.append(s).flush();
    }

    private static void checkNotBlank(String content) throws BlankContentException {
        if (StringUtils.isBlank(content)) {
            throw new BlankContentException();
        }
    }


    private static class BlankContentException extends Exception {
    }
}
