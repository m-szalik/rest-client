package org.jsoftware.restclient.plugins;

import org.apache.commons.lang3.StringUtils;
import org.jsoftware.restclient.RestClientPlugin;

/**
 * Add <code>Authorization</code> header with value <code>Bearer<i>token</i></code>
 * @author szalik
 */
public class AuthBearerPlugin implements RestClientPlugin {
    private String token;

    public AuthBearerPlugin() {
        this(null);
    }

    public AuthBearerPlugin(String token) {
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
