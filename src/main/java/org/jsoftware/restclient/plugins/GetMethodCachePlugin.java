package org.jsoftware.restclient.plugins;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoftware.restclient.RestClientPlugin;
import org.jsoftware.restclient.RestClientResponse;
import org.jsoftware.utils.SimpleCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Cache get method calls
 * @author szalik
 */
public class GetMethodCachePlugin implements RestClientPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SimpleCache<String,RestClientResponse> cache;

    private final AtomicLong hits = new AtomicLong(), misses = new AtomicLong();

    /**
     * @param timeoutMillis cache ttl im milliseconds
     * @param size cache size
     * @param clock to get <code>now</code>
     */
    protected GetMethodCachePlugin(long timeoutMillis, int size, Clock clock) {
        this.cache = new SimpleCache<String, RestClientResponse>(timeoutMillis, size) {
            @Override
            protected Instant now() {
                return clock.instant();
            }
        };
    }


    /**
     * @param timeoutMillis cache ttl im milliseconds
     * @param size cache size
     */
    public GetMethodCachePlugin(long timeoutMillis, int size) {
        this(timeoutMillis, size, Clock.systemDefaultZone());
    }

    /**
     * Create cache with default size 128 entries
     * @param timeoutMillis cache ttl im milliseconds
     */
    public GetMethodCachePlugin(long timeoutMillis) {
        this(timeoutMillis, 128);
    }



    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        if ("GET".equalsIgnoreCase(context.getRequest().getMethod())) {
            String key = context.getURI();
            RestClientResponse cr = cache.get(key);
            boolean fetch = cr == null;
            if (! fetch) {
                fetch = headerEq(context.getRequest(), "Cache-Control", "no-cache") || headerEq(context.getRequest(), "Pragma", "no-cache");
            }
            if (fetch) {
                if (logger.isTraceEnabled()) {
                    if (cr == null) {
                        logger.trace("Response for {} not found in cache.", context.getRequest());
                    } else {
                        logger.trace("Response for {} found in cache, but it is expired or requested by setting http request header.", context.getRequest());
                    }
                }
                misses.incrementAndGet();
                chain.continueChain();
                RestClientResponse restClientResponse = context.getResponse();
                if (restClientResponse == null) {
                    throw new IllegalStateException("Http Response is null for " + context.getRequest());
                }
                cache.put(key, restClientResponse);
                logger.trace("Response for {} put into cache.", context.getRequest());
            } else {
                logger.trace("Response for {} fetched from cache.", context.getRequest());
                hits.incrementAndGet();
                context.setResponse(cr);
            }
        } else {
            chain.continueChain();
        }
    }

    /**
     * Check if request contains header with value
     * @param request request
     * @param headerName header name
     * @param headerValue header value to check
     */
    private boolean headerEq(HttpRequestBase request, String headerName, String headerValue) {
        Header[] headers = request.getAllHeaders();
        if (headers != null) {
            for(Header h : headers) {
                if (headerName.equalsIgnoreCase(h.getName()) && headerValue.equalsIgnoreCase(h.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Clear cache content and cache statistics
     */
    public void clearCache() {
        cache.clear();
        misses.set(0);
        hits.set(0);
    }

    public CacheStatistics getStatistics() {
        return new CacheStatistics() {
            @Override
            public long getHits() {
                return hits.get();
            }
            @Override
            public long getMisses() {
                return misses.get();
            }
        };
    }

    /**
     * Cache statistics
     * @author szalik
     */
    public interface CacheStatistics {
        /**
         * @return cache hits
         */
        long getHits();
        /**
         * @return cache misses
         */
        long getMisses();
    }
}


