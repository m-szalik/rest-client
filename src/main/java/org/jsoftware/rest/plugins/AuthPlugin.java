package org.jsoftware.rest.plugins;

import org.jsoftware.rest.HttpClientPlugin;

/**
 * Created by mgruszecki on 02.12.15.
 */
public class AuthPlugin implements HttpClientPlugin {
    private final String token;

    public AuthPlugin(String token) {
        this.token = token;
    }



    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        context.getRequest().addHeader("Authorization", "Bearer " + token);
        chain.continueChain();
    }
}
