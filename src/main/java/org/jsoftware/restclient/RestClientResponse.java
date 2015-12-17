package org.jsoftware.restclient;

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
 * Http response
 * @author szalik
 */
public interface RestClientResponse extends Serializable {

    /**
     * @return http status line
     */
    StatusLine getStatusLine();

    /**
     * @return response http headers
     */
    Header[] getAllHeaders();

    /**
     * @return response body as String
     * @throws IOException
     */
    String getContent() throws IOException;

    /**
     * Parse response body as json
     * @param path json path
     * @return String, Number, List...
     * @throws IOException
     * @see https://github.com/jayway/JsonPath
     */
    Object json(String path) throws IOException;

    /**
     * Parse response body as xml
     * @see javax.xml.xpath.XPathConstants
     */
    Object xPath(String xPath, QName type) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException;

    /**
     * Parse response body as xml
     */
    String xPath(String xPath) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException;

    /**
     * Dump response to PrintStream
     * @param withHeaders add headers if true
     * @param to output stream
     * @throws IOException
     */
    void dump(boolean withHeaders, PrintStream to) throws IOException;

    /**
     * Dump response to stdout
     * @param withHeaders add headers if true
     * @throws IOException
     */
    default void dump(boolean withHeaders) throws IOException {
        dump(withHeaders, System.out);
    }
}
