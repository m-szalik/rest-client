package org.jsoftware.rest.plugins;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoftware.rest.RestClientResponse;
import org.jsoftware.rest.HttpClientPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class VerbosePlugin implements HttpClientPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        StringBuilder s = new StringBuilder();
        HttpRequestBase request = context.getRequest();
        s.append("> ").append(request.getMethod()).append(' ').append(request.getURI()).append('\n');
        if (request instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase r = (HttpEntityEnclosingRequestBase) request;
            HttpEntity entity = r.getEntity();
            if (entity!=null) {
                s.append("> ").append(IOUtils.toString(entity.getContent())).append('\n');
            }
        }
        try {
            chain.continueChain();
            RestClientResponse response = context.getResponse();
            s.append("< ").append(response.getStatusLine()).append("\n< ").append(response.getContent());
            logger.info(s.toString());
        } catch (Exception ex) {
            s.append("* ").append(ex);
            logger.error(s.toString());
            throw ex;
        }
    }
}
