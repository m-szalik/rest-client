package org.jsoftware.restclient.plugins;

import org.apache.http.client.methods.HttpRequestBase;
import org.jsoftware.restclient.RestClientPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

/**
 * Log response time.
 * @author szalik
 */
public class RequestTimePlugin implements RestClientPlugin {
    private final Clock clock;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public RequestTimePlugin() {
        this(Clock.systemDefaultZone());
    }

    protected RequestTimePlugin(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        long start = clock.millis();
        try {
            chain.continueChain();
        } finally {
            long time = clock.millis() - start;
            HttpRequestBase request = context.getRequest();
            logTime(request, time);
        }
    }

    protected void logTime(HttpRequestBase request, long timeMs) {
        logger.info("Request time for {} {} was {}ms.", request.getMethod(), request.getURI(), timeMs);
    }
}
