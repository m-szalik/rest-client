package org.jsoftware.restclient.plugins;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.jsoftware.restclient.RestClientPlugin;

import java.nio.charset.Charset;

/**
 * Add <code>Authorization</code> header with value <code>Basic Authorization</code>
 * @author szalik
 */
public class AuthBasicPlugin implements RestClientPlugin {
    private final static Charset UTF = Charset.forName("UTF-8");
    private final Header authHeader;

    public AuthBasicPlugin(@NotNull String username, @NotNull String password) {
        this.authHeader = createBasicAuthorizationHeader(username, password);
    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        context.getRequest().addHeader(authHeader);
        chain.continueChain();
    }

    @NotNull
    public static Header createBasicAuthorizationHeader(@NotNull String username, @NotNull String password) {
        byte[] encoding = Base64.encodeBase64((username + ":" + password).getBytes(UTF));
        String str = new String(encoding, UTF);
        return new BasicHeader("Authorization", "Basic " + str);
    }

}
