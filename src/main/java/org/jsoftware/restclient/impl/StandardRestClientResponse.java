package org.jsoftware.restclient.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.jsoftware.restclient.BinaryContent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author szalik
 */
class StandardRestClientResponse extends AbstractStandardRestClientResponse {
    private final HttpResponse httpResponse;
    private BinaryContent binaryContent;

    StandardRestClientResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public Header[] getAllHeaders() {
        return httpResponse.getAllHeaders();
    }


    @Override
    public synchronized BinaryContent getBinaryContent() throws IOException {
        if (binaryContent == null) {
            HttpEntity data = httpResponse.getEntity();
            if (data == null) {
                throw new IOException("No content can be found.");
            }
            binaryContent = new StandardRestClientResponseBinaryContent(data);
        }
        return binaryContent;
    }


    @Override
    public StatusLine getStatusLine() {
        return httpResponse.getStatusLine();
    }

}

class StandardRestClientResponseBinaryContent implements BinaryContent {
    private static final int BUFFER_SIZE = 1024 * 1024;
    private final HttpEntity entity;
    private final Optional<String> contentType;
    private Optional<Long> length;
    private byte[] buffer;

    StandardRestClientResponseBinaryContent(HttpEntity entity) {
        String contentType = null;
        if (entity.getContentType() != null && StringUtils.isNotBlank(entity.getContentType().getValue())) {
            contentType = entity.getContentType().getValue();
        }
        this.entity = entity;
        this.length = entity.getContentLength() >= 0 ? Optional.of(entity.getContentLength()) : Optional.empty();
        this.contentType = Optional.ofNullable(contentType);
    }

    @Override
    public synchronized InputStream getStream() throws IOException {
        if (buffer != null) {
            return new ByteArrayInputStream(buffer);
        }
        if (canUseBuffer()) {
            try (InputStream inb = entity.getContent()) {
                buffer = IOUtils.toByteArray(inb);
            }
            if (! length.isPresent()) {
                length = Optional.of(Long.valueOf(buffer.length));
            }
            return new ByteArrayInputStream(buffer);
        }
        return entity.getContent();
    }

    @Override
    public boolean isRepeatable() {
        return buffer != null || canUseBuffer() || entity.isRepeatable();
    }

    @Override
    public Optional<Long> getLength() {
        return length;
    }

    @Override
    public Optional<String> getContentType() {
        return contentType;
    }

    private boolean canUseBuffer() {
        boolean b = length.isPresent() && length.get() < BUFFER_SIZE;
        if (! b && getContentType().isPresent()) {
            String contentType = getContentType().get();
            b = StringUtils.containsIgnoreCase(contentType, "text");
        }
        return b;
    }

    @Override
    public void close() {
        try {
            EntityUtils.consume(entity);
        } catch (IOException e) {
            // ignore
        }
    }
}