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
class StandardRestClientResponse extends AbstractStandardRestClientResponse {
    private final HttpResponse httpResponse;
    private String content;

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

}
