package org.jsoftware.restclient.plugins;

import org.apache.commons.lang3.StringUtils;
import org.jsoftware.restclient.RestClientPlugin;

/**
 * Add custom header to request
 * @author szalik
 */
public class CustomRequestHeaderPlugin implements RestClientPlugin {
    private final String headerName;
    private String headerValue;
    private boolean disabled;

    /**
     * Add header tho http request
     * @param headerName header name
     * @param headerValue initial value of header field
     */
    public CustomRequestHeaderPlugin(String headerName, String headerValue) {
        this.headerName = headerName;
        if (StringUtils.isBlank(headerName)) {
            throw new IllegalArgumentException("Parameter 'headerName' cannot be blank");
        }
        setHeaderValue(headerValue);
    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        if (! disabled) {
            context.getRequest().addHeader(headerName, headerValue);
        }
        chain.continueChain();
    }

    /**
     * Update header value
     * @param headerValue new value of the header
     */
    public void setHeaderValue(String headerValue) {
        if (StringUtils.isBlank(headerValue)) {
            throw new IllegalArgumentException("Parameter 'headerValue' cannot be blank." +
                    " If you want to disable this header invoke disable() method.");
        }
        this.headerValue = headerValue;
    }

    /**
     * Disable the header
     */
    public void disable() {
        disabled = true;
    }

    /**
     * Enable the header
     */
    public void enable() {
        disabled = false;
    }

    /**
     * @return true if header is enabled
     */
    public boolean isEnabled() {
        return ! disabled;
    }
}
