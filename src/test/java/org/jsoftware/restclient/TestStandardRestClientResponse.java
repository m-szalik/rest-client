package org.jsoftware.restclient;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.jsoftware.restclient.impl.AbstractStandardRestClientResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author szalik
 */
public class TestStandardRestClientResponse extends AbstractStandardRestClientResponse {
    private final StatusLine statusLine;
    private final String content;
    private final Header[] headers;

    public TestStandardRestClientResponse(String content) {
        this(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "Code " + 200), content);
    }

    public TestStandardRestClientResponse(StatusLine statusLine, String content, Header... headers) {
        this.statusLine = statusLine;
        this.content = content;
        this.headers = headers == null ? new Header[]{} : headers;
    }

    @Override
    public StatusLine getStatusLine() {
        return statusLine;
    }

    @Override
    public Header[] getAllHeaders() {
        return headers;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new RuntimeException("Not supported");
    }

    @Override
    public String getContent() throws IOException {
        return content;
    }

}
