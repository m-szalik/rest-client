package org.jsoftware.rest.plugins;

import org.jsoftware.rest.RestClientResponse;
import org.jsoftware.rest.HttpClientPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class GetMethodCachePlugin implements HttpClientPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final long timeoutMillis;
    private final MyLRUCache<String,CacheEntry> cache = new MyLRUCache<>(128);

    public GetMethodCachePlugin(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        if ("GET".equalsIgnoreCase(context.getRequest().getMethod())) {
            String key = context.getRequest().getURI().toString();
            CacheEntry ce = cache.get(key);
            long now = System.currentTimeMillis();
            if (ce == null || ce.getTimeout() < now) {
                chain.continueChain();
                RestClientResponse restClientResponse = context.getResponse();
                if (restClientResponse == null) {
                    throw new IllegalStateException("Http Response is null for " + context.getRequest());
                }
                cache.put(key, new CacheEntry(now+timeoutMillis, restClientResponse));
                logger.trace("Response for {} put into cache.", context.getRequest());
            } else {
                logger.trace("Response for {} fetched from cache.", context.getRequest());
                context.setResponse(ce.getResponse());
            }
        } else {
            chain.continueChain();
        }
    }
}

class MyLRUCache<K, V> extends LinkedHashMap<K, V> {
    private int cacheSize;

    public MyLRUCache(int cacheSize) {
        super(16, 0.75f, true);
        this.cacheSize = cacheSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() >= cacheSize;
    }
}

class CacheEntry implements Serializable {
    private final long timeout;
    private final RestClientResponse response;

    CacheEntry(long timeout, RestClientResponse response) {
        this.timeout = timeout;
        this.response = response;
    }

    public long getTimeout() {
        return timeout;
    }

    public RestClientResponse getResponse() {
        return response;
    }
}