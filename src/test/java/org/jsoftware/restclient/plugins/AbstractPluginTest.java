package org.jsoftware.restclient.plugins;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicStatusLine;
import org.jsoftware.restclient.RestClientPlugin;
import org.jsoftware.restclient.RestClientResponse;
import org.jsoftware.restclient.impl.AbstractStandardRestClientResponse;

import java.io.IOException;
import java.util.function.Function;

/**
 */
public class AbstractPluginTest {

    protected RestClientResponse call(RestClientPlugin plugin, HttpRequestBase request, Function<HttpRequestBase, RestClientResponse> responseFunction) throws Exception {
        TestPluginContext pc = new TestPluginContext();
        pc.setRequest(request);
        plugin.plugin(pc, () -> {
            RestClientResponse resp = responseFunction.apply(pc.getRequest());
            pc.setResponse(resp);
        });
        return pc.getResponse();
    }

    protected RestClientResponse stdResponse(int httpCode, String content, Header... headers) {
        BasicStatusLine sl = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), httpCode, "Code " + httpCode);
        return new TestRestClientResponse(sl, content, headers);
    }

    protected HttpGet get(String url) {
        return new HttpGet(url);
    }
}


class TestPluginContext implements RestClientPlugin.PluginContext {
    private HttpRequestBase request;
    private RestClientResponse response;

    @Override
    public HttpRequestBase getRequest() {
        return request;
    }

    @Override
    public RestClientResponse getResponse() {
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
}

class TestRestClientResponse extends AbstractStandardRestClientResponse {
    private final StatusLine statusLine;
    private final String content;
    private final Header[] headers;

    TestRestClientResponse(StatusLine statusLine, String content, Header... headers) {
        this.statusLine = statusLine;
        this.content = content;
        this.headers = headers == null ? new Header[]{} : headers;
    }

    @Override
    public StatusLine getStatusLine() {
        return statusLine;
    }

    @Override
    public Header[] getAllHeaders() {
        return headers;
    }

    @Override
    public String getContent() throws IOException {
        return content;
    }
}