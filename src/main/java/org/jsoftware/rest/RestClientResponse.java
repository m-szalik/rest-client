package org.jsoftware.rest;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * @author szalik
 */
public interface RestClientResponse extends Serializable {

    Header[] getAllHeaders();

    StatusLine getStatusLine();

    String getContent() throws IOException;

    Object json(String path) throws IOException;

    /**
     * @see javax.xml.xpath.XPathConstants
     */
    Object xPath(String xPath, QName type) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException;

    String xPath(String xPath) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException;

    void dump(boolean withHeaders, PrintStream to) throws IOException;

    default void dump(boolean withHeaders) throws IOException {
        dump(withHeaders, System.out);
    }
}
