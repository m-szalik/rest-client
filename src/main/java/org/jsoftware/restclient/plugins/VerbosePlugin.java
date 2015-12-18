package org.jsoftware.restclient.plugins;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoftware.restclient.RestClientPlugin;
import org.jsoftware.restclient.RestClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

/**
 * Display request and response content.
 * <p>Use static factory method to obtain an instance.</p>
 * @author szalik
 */
public class VerbosePlugin implements RestClientPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PrintStream[] output;
    private final boolean logsOutput;

    /**
     * Create plugin instance
     * @param logsOutput it true output goes to slf4j logs
     * @param outputs array of PrintStreams to send output there
     */
    public VerbosePlugin(boolean logsOutput, PrintStream... outputs) {
        this.output = outputs == null ? new PrintStream[0] : outputs;
        this.logsOutput = logsOutput;
        logger.debug("Creating {} with logsOutput={} and {}", getClass(), logsOutput, StringUtils.join(this.output, ','));
    }

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
            int code = response.getStatusLine().getStatusCode();
            boolean error = code >= 400;
            print(s, error);
        } catch (Exception ex) {
            s.append("* ").append(ex);
            print(s, true);
            throw ex;
        }
    }

    private void print(StringBuilder output, boolean error) {
        for (PrintStream ps : this.output) {
            ps.println(output.toString());
            ps.flush();
        }
        if (logsOutput) {
            if (error) {
                logger.warn(output.toString());
            } else {
                logger.info(output.toString());
            }
        }
    }

}
