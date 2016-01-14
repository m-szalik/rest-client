package org.jsoftware.restclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Access to binary content of http response.
 * @author szalik
 */
public interface BinaryContent extends AutoCloseable {

    /**
     * @return binary data stream
     */
    InputStream getStream() throws IOException;

    /**
     * @return true if stream can be opened more then one time.
     */
    boolean isRepeatable();

    /**
     * @return content length
     */
    Optional<Long> getLength();

    /**
     * @return content type;
     */
    Optional<String> getContentType();

    @Override
    void close(); // override - no exceptions

}
