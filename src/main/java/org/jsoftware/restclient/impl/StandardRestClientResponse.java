package org.jsoftware.restclient.impl;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.jsoftware.restclient.RestClientResponse;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;

/**
 * @author szalik
 */
class StandardRestClientResponse implements RestClientResponse {
    private final HttpResponse httpResponse;
    private String content;
    private DocumentContext json;
    private Document xmlDocument;

    StandardRestClientResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public Header[] getAllHeaders() {
        return httpResponse.getAllHeaders();
    }

    @Override
    public StatusLine getStatusLine() {
        return httpResponse.getStatusLine();
    }

    @Override
    public synchronized String getContent() throws IOException {
        if (content == null) {
            HttpEntity data = httpResponse.getEntity();
            try {
                InputStream is = data.getContent();
                content = IOUtils.toString(is);
            } finally {
                EntityUtils.consume(data);
            }
        }
        return content;
    }

    @Override
    public synchronized Object json(String path) throws IOException {
        if (json == null) {
            json = JsonPath.parse(getContent());
        }
        return json.read(path);
    }


    private synchronized XPathExpression xPathInternal(String xPath) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        if (xmlDocument == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlDocument = builder.parse(new InputSource(new StringReader(getContent())));
        }
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        return xpath.compile(xPath);
    }


    @Override
    public Object xPath(String xPath, QName type) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        XPathExpression expr = xPathInternal(xPath);
        return expr.evaluate(xmlDocument, type);
    }

    @Override
    public String xPath(String xPath) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        XPathExpression expr = xPathInternal(xPath);
        return expr.evaluate(xmlDocument);
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
}
