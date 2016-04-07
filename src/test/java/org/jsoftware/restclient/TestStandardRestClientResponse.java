package org.jsoftware.restclient;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.jsoftware.restclient.impl.AbstractStandardRestClientResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author szalik
 */
public class TestStandardRestClientResponse extends AbstractStandardRestClientResponse {
    private final StatusLine statusLine;
    private final Header[] headers;
    private final TestStandardRestClientResponseBinaryContent binaryContent;


    public TestStandardRestClientResponse(String content) {
        this(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "Code " + 200), content);
    }

    public TestStandardRestClientResponse(StatusLine statusLine, byte[] content, boolean contentRepeatable, Header... headers) {
        this.statusLine = statusLine;
        this.headers = headers == null ? new Header[]{} : headers;
        this.binaryContent = new TestStandardRestClientResponseBinaryContent(content, contentRepeatable);
    }

    public TestStandardRestClientResponse(BasicStatusLine statusLine, String content, Header... headers) {
        this(statusLine, content.getBytes(), true, headers);
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
    public BinaryContent getBinaryContent() throws IOException {
        return binaryContent;
    }

}

class TestStandardRestClientResponseBinaryContent implements BinaryContent {
    private final byte[] data;
    private final boolean repeatable;
    private boolean inUse;

    TestStandardRestClientResponseBinaryContent(byte[] data, boolean repeatable) {
        this.data = data;
        this.repeatable = repeatable;
    }

    @Override
    public InputStream getStream() throws IOException {
        if (! repeatable && inUse) {
            throw new IOException("Cannot read stream twice.");
        }
        inUse = true;
        return new ByteArrayInputStream(data);
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    @Override
    public Optional<Long> getLength() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getContentType() {
        return Optional.empty();
    }

    @Override
    public void close() {
        // nothing to do here
    }

}