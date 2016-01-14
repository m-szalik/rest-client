package org.jsoftware.restclient.plugins;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicStatusLine;
import org.jsoftware.restclient.RestClientPlugin;
import org.jsoftware.restclient.RestClientResponse;
import org.jsoftware.restclient.TestStandardRestClientResponse;

import java.net.URI;
import java.util.function.Function;

/**
 */
public class AbstractPluginTest {

    protected RestClientResponse call(RestClientPlugin plugin, HttpRequestBase request, Function<HttpRequestBase, RestClientResponse> responseFunction) throws Exception {
        TestPluginContext pc = new TestPluginContext();
        pc.setURI(request.getURI().toASCIIString());
        pc.setRequest(request);
        plugin.plugin(pc, () -> {
            request.setURI(new URI(pc.getURI()));
            RestClientResponse resp = responseFunction.apply(pc.getRequest());
            pc.setResponse(resp);
        });
        return pc.getResponse();
    }

    protected RestClientResponse stdResponse(int httpCode, String content, Header... headers) {
        BasicStatusLine sl = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), httpCode, "Code " + httpCode);
        return new TestStandardRestClientResponse(sl, content, headers);
    }

    protected HttpGet get(String url) {
        return new HttpGet(url);
    }
}


class TestPluginContext implements RestClientPlugin.PluginContext {
    private HttpRequestBase request;
    private RestClientResponse response;
    private String uri;

    @Override
    public HttpRequestBase getRequest() {
        return request;
    }

    @Override
    public RestClientResponse getResponse() {
        if (response == null) {
            throw new IllegalArgumentException();
        }
        return response;
    }

    @Override
    public void setRequest(HttpRequestBase request) {
        this.request = request;
    }

    @Override
    public void setResponse(RestClientResponse response) {
        this.response = response;
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public void setURI(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean isResponseAvailable() {
        return response != null;
    }
}

