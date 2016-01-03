package org.jsoftware.restclient.plugins;

import org.apache.http.client.methods.HttpGet;
import org.jsoftware.restclient.RestClientResponse;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * @author szalik
 */
public class GetMethodCachePluginTest extends AbstractPluginTest {
    private TestClock clock;
    private GetMethodCachePlugin plugin;

    @Before
    public void setUp() throws Exception {
        clock = new TestClock();
        plugin = new GetMethodCachePlugin(10000, 5, clock) {};
    }

    @Test
    public void fromCache() throws Exception {
        final String URI = "http://story.com/my/";
        final AtomicInteger counter = new AtomicInteger(0);
        RestClientResponse resp;
        resp = call(plugin, get(URI), (r)->{
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        String value1 = resp.getContent();
        resp = call(plugin, get(URI), (r)->{
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });

        assertEquals(value1, resp.getContent());
        assertEquals(1, counter.get());
    }


    @Test
    public void testNoCacheHeader() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        HttpGet get = new HttpGet("http://story.com/my/");
        call(plugin, get, (r)->{
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        call(plugin, get, (r)->{
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        assertEquals(1, counter.get());

        get.setHeader("Cache-Control", "no-cache");
        call(plugin, get, (r) -> {
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        assertEquals(2, counter.get());
    }


    @Test
    public void testClearCache() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        HttpGet get = new HttpGet("http://story.com/my/");
        call(plugin, get, (r)->{
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        call(plugin, get, (r)->{
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        assertEquals(1, counter.get());

        plugin.clearCache();
        call(plugin, get, (r) -> {
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        assertEquals(2, counter.get());
    }


    @Test
    public void testExpire() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        HttpGet get = new HttpGet("http://story.com/my/");
        call(plugin, get, (r)->{
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        call(plugin, get, (r)->{
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        assertEquals(1, counter.get());

        clock.setInstant(clock.instant().plus(7, ChronoUnit.MINUTES));
        call(plugin, get, (r) -> {
            counter.incrementAndGet();
            return stdResponse(200, UUID.randomUUID().toString());
        });
        assertEquals(2, counter.get());
    }
}

class TestClock extends Clock {
    private final ZoneId zoneId = ZoneId.systemDefault();
    private Instant instant = Instant.now();
    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        throw new IllegalStateException();
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }
}