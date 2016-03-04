package org.jsoftware.restclient.plugins;

import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class RequestTimePluginTest extends AbstractPluginTest {

    @Test
    public void testTimeMeasurement() throws Exception {
        final AtomicLong time = new AtomicLong(0);
        final TestClock clock = new TestClock();
        RequestTimePlugin plugin = new RequestTimePlugin(clock) {
            @Override
            protected void logTime(HttpRequestBase request, long timeMs) {
                super.logTime(request, timeMs);
                time.set(timeMs);
            }
        };

        call(plugin, get("http://nowhere.com"), (r) -> {
            Instant instant = clock.instant().plusSeconds(3);
            clock.setInstant(instant);
            return stdResponse(200, "OK");
        });

        Assert.assertEquals(3000, time.get());
    }
}