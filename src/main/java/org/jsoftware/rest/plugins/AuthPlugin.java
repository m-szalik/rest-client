package org.jsoftware.rest.plugins;

import org.apache.commons.lang3.StringUtils;
import org.jsoftware.rest.HttpClientPlugin;

/**
 */
public class AuthPlugin implements HttpClientPlugin {
    private String token;

    public AuthPlugin() {
        this(null);
    }

    public AuthPlugin(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        if (StringUtils.isNotBlank(token)) {
            context.getRequest().addHeader("Authorization", "Bearer " + token);
        }
        chain.continueChain();
    }
}
