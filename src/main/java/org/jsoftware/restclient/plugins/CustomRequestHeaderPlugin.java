package org.jsoftware.restclient.plugins;

import org.apache.commons.lang3.StringUtils;
import org.jsoftware.restclient.RestClientPlugin;

/**
 * Add custom header to request
 * @author szalik
 */
public class CustomRequestHeaderPlugin implements RestClientPlugin {
    private final String headerName, headerValue;

    public CustomRequestHeaderPlugin(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
        if (StringUtils.isBlank(headerName)) {
            throw new IllegalArgumentException("Parameter 'headerName' cannot be blank");
        }
        if (StringUtils.isBlank(headerValue)) {
            throw new IllegalArgumentException("Parameter 'headerValue' cannot be blank");
        }
    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        context.getRequest().addHeader(headerName, headerValue);
        chain.continueChain();
    }
}
