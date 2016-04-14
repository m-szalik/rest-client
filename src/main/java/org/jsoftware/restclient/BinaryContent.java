package org.jsoftware.restclient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Access to binary content of http response.
 * @author szalik
 */
public interface BinaryContent extends AutoCloseable {

    /**
     * @throws IOException io error occurred
     * @return binary data stream
     */
    @NotNull InputStream getStream() throws IOException;

    /**
     * @return true if stream can be opened more then one time.
     */
    boolean isRepeatable();

    /**
     * @return content length
     */
    @NotNull Optional<Long> getLength();

    /**
     * @return content type;
     */
    @NotNull Optional<String> getContentType();

    @Override
    void close(); // override - no exceptions

}
