package org.jsoftware.restclient;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.jsoup.select.Elements;

import javax.xml.namespace.QName;
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
     * @throws IOException if http response cannot be read
     * @return response body as String
     */
    String getContent() throws IOException;

    /**
     * Parse response body as json
     * @param path json path
     * @return String, Number, List...
     * @throws IOException if response cannot be read, is not valid json
     * @throws InvalidContentException if content is not valid JSON document
     * @throws PathNotFoundException if requested path cannot be found
     * @see <a href="https://github.com/jayway/JsonPath">Jayway</a>
     */
    Object json(String path) throws IOException, PathNotFoundException, InvalidContentException;

    /**
     * Parse response body as xml
     * @see javax.xml.xpath.XPathConstants
     * @param xPath xPath definition
     * @param type requested xPath type
     * @return requested value
     * @throws IOException if response cannot be read
     * @throws InvalidContentException if content is not valid XML document
     * @throws XPathExpressionException if argument <code>xPath</code> is not valid xPath definition
     * @throws PathNotFoundException if requested path cannot be found
     *
     */
    Object xPath(String xPath, QName type) throws IOException, XPathExpressionException, PathNotFoundException, InvalidContentException;

    /**
     * Parse response body as xml
     * @param xPath xPath definition
     * @return requested value
     * @throws IOException if response cannot be read or is not valid XML
     * @throws XPathExpressionException if argument <code>xPath</code> is not valid xPath definition
     * @throws InvalidContentException if content is not valid XML document
     * @throws PathNotFoundException if requested path cannot be found
     */
    String xPath(String xPath) throws IOException, XPathExpressionException, PathNotFoundException, InvalidContentException;

    Elements html(String jQueryExpression) throws IOException;


    /**
     * Dump response to PrintStream
     * @param withHeaders add headers if true
     * @param to output stream
     * @throws IOException if response cannot be read
     */
    void dump(boolean withHeaders, PrintStream to) throws IOException;

    /**
     * Dump response to stdout
     * @param withHeaders add headers if true
     * @throws IOException if response cannot be read
     */
    default void dump(boolean withHeaders) throws IOException {
        dump(withHeaders, System.out);
    }
}
