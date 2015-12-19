package org.jsoftware.restclient.plugins;

import org.jsoftware.restclient.RestClientPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * If request's url doesn't start with <code>http://</code> or <code>https://</code> then add baseURL as URL prefix.
 * <p>
 *     Example:<br/>
 *     if baseURL equals &quot;http://somewhere.com/api&quot;
 *     than <code>restClient.get(&quot;users&quot;)</code> will be rewrite to &quot;http://somewhere.com/api/method&quot;.<br/>
 *     <strong>Plugins are ordered as it was added so remember to put this plugin at the begging.</strong>
 * </p>
 * @author szalik
 */
public class BaseURLPlugin implements RestClientPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String baseURL;

    /**
     * @param baseURL BaseURL
     * @throws IllegalArgumentException if baseURL is not valid URL
     */
    public BaseURLPlugin(String baseURL) throws IllegalArgumentException {
        try {
            new URL(baseURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Argument is not valid URL - '" +baseURL + "'", e);
        }
        this.baseURL = baseURL;
    }

    @Override
    public void plugin(RestClientPlugin.PluginContext context, RestClientPlugin.PluginChain chain) throws Exception {
        String current = context.getRequest().getURI().toASCIIString();
        String url = current;
        String urlLowerCase = current.toLowerCase();
        if (! urlLowerCase.startsWith("http://") && ! urlLowerCase.startsWith("https://")) {
            url = baseURL + (url.startsWith("/") ? url.substring(1) : url);
        }
        if (! url.equals(current)) {
            logger.debug("URL changed from '{}' to '{}'", current, url);
            context.getRequest().setURI(new URI(url));
        }
    }
}
