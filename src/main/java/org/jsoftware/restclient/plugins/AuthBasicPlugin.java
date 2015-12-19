package org.jsoftware.restclient.plugins;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jsoftware.restclient.RestClientPlugin;
import org.jsoftware.restclient.RestClientResponse;

/**
 * Add <code>Authorization</code> header with value <code>Basic Authorization</code>
 * @author szalik
 */
public class AuthBasicPlugin implements RestClientPlugin {
    private final String auth;

    public AuthBasicPlugin(String username, String password) {
        byte[] encoding = Base64.encodeBase64((username + ":" + password).getBytes());
        this.auth = new String(encoding);
    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        context.getRequest().addHeader("Authorization", "Basic " + auth);
        chain.continueChain();
    }

}
