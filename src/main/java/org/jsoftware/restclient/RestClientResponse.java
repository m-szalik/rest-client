package org.jsoftware.restclient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.select.Elements;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

/**
 * Http response
 * @author szalik
 */
public interface RestClientResponse extends Serializable {

    interface ResponseStatus {
        int getStatusCode();
        String getReasonPhrase();
    }
    /**
     * @return http status line
     */
    @NotNull ResponseStatus getStatusLine();

    /**
     * @return response http headers
     */
    @NotNull Collection<HttpHeader> getAllHeaders();

    /**
     * @return http response body as binary stream
     * @throws IOException when content is not available
     */
    @NotNull BinaryContent getBinaryContent() throws IOException;

    /**
     * @throws IOException if http response cannot be read or if content was already used by #getInputStream
     * @return response body as String
     */
    @NotNull String getContent() throws IOException;

    /**
     * Parse response body as json
     * @param path json path
     * @return String, Number, List...
     * @throws IOException if response cannot be read, is not valid json
     * @throws InvalidTypeOfContentException if content is not valid JSON document
     * @throws PathNotFoundException if requested path cannot be found
     * @throws IllegalArgumentException if path is null
     * @see <a href="https://github.com/jayway/JsonPath">Jayway</a>
     */
    @Nullable Object json(@NotNull String path) throws IOException, PathNotFoundException, InvalidTypeOfContentException;

    /**
     * Parse response body as xml
     * @see javax.xml.xpath.XPathConstants
     * @param xPath xPath definition
     * @param type requested xPath type
     * @return requested value
     * @throws IOException if response cannot be read
     * @throws InvalidTypeOfContentException if content is not valid XML document
     * @throws XPathExpressionException if argument <code>xPath</code> is not valid xPath definition
     * @throws PathNotFoundException if requested path cannot be found
     *
     */
    @NotNull Object xPath(@NotNull String xPath, @NotNull QName type) throws IOException, XPathExpressionException, PathNotFoundException, InvalidTypeOfContentException;

    /**
     * Parse response body as xml
     * @param xPath xPath definition
     * @return requested value
     * @throws IOException if response cannot be read or is not valid XML
     * @throws XPathExpressionException if argument <code>xPath</code> is not valid xPath definition
     * @throws InvalidTypeOfContentException if content is not valid XML document
     * @throws IllegalArgumentException if xPath is null
     * @throws PathNotFoundException if requested path cannot be found
     */
    @NotNull String xPath(@NotNull String xPath) throws IOException, XPathExpressionException, PathNotFoundException, InvalidTypeOfContentException;

    /**
     *
     * @param jQueryExpression an expression (jQuery style)
     * @return Elements for that jQueryExpression
     * @throws IOException error during content reading
     * @throws IllegalArgumentException if jQueryExpression is null
     * @see org.jsoup.Jsoup
     */
    @NotNull Elements html(@NotNull String jQueryExpression) throws IOException;


    /**
     * Dump response to PrintStream
     * @param withHeaders add headers if true
     * @param to output stream
     * @throws IOException if response cannot be read
     */
    void dump(boolean withHeaders, @Nullable PrintStream to) throws IOException;

    /**
     * Dump response to stdout
     * @param withHeaders add headers if true
     * @throws IOException if response cannot be read
     */
    default void dump(boolean withHeaders) throws IOException {
        dump(withHeaders, System.out);
    }

    /**
     * @param headerName header name
     * @return response header value
     */
    default Optional<String> getHeader(String headerName) {
        Collection<HttpHeader> headers = getAllHeaders();
        if (headers != null) {
            for(HttpHeader h : headers) {
                if (headerName.equalsIgnoreCase(h.getName())) {
                    return Optional.of(h.getValue());
                }
            }
        }
        return Optional.empty();
    }
}
