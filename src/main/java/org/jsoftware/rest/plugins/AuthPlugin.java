package org.jsoftware.rest.plugins;

import org.apache.commons.lang3.StringUtils;
import org.jsoftware.rest.RestClientPlugin;

/**
 */
public class AuthPlugin implements RestClientPlugin {
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
    public void clearToken() {
        this.token = null;
    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        if (StringUtils.isNotBlank(token)) {
            context.getRequest().addHeader("Authorization", "Bearer " + token);
        }
        chain.continueChain();
    }
}
