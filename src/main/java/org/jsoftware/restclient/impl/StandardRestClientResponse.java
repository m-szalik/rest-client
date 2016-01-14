package org.jsoftware.restclient.impl;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author szalik
 */
class StandardRestClientResponse extends AbstractStandardRestClientResponse {
    private final HttpResponse httpResponse;
    private String content;
    private boolean inputStreamUsed;

    StandardRestClientResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public Header[] getAllHeaders() {
        return httpResponse.getAllHeaders();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        HttpEntity data = httpResponse.getEntity();
        if (data == null) {
            throw new IOException("No content can be found.");
        }
        InputStream is = data.getContent();
        return is;
    }

    @Override
    public StatusLine getStatusLine() {
        return httpResponse.getStatusLine();
    }

    @Override
    public synchronized String getContent() throws IOException {
        if (content == null) {
            if (inputStreamUsed) {
                throw new IOException("Data already used by getInput()");
            }
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
