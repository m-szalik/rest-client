package org.jsoftware.restclient.plugins;

import org.apache.commons.io.IOUtils;
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
    private final boolean consoleOutput, logsOutput;

    private VerbosePlugin(boolean consoleOutput, boolean logsOutput) {
        this.consoleOutput = consoleOutput;
        this.logsOutput = logsOutput;
        logger.debug("Creating {} with consoleOutput={}, logsOutput={}", getClass(), consoleOutput, logsOutput);
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
        if (consoleOutput) {
            PrintStream ps = error ? System.err : System.out;
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

    /**
     * Instance that sends output to console
     */
    public static VerbosePlugin consoleOutputOnly() {
        return new VerbosePlugin(true, false);
    }

    /**
     * Instance that sends output to logs
     */
    public static VerbosePlugin logsOutputOnly() {
        return new VerbosePlugin(false, true);
    }

    /**
     * Instance that sends output to console and logs
     */
    public static VerbosePlugin consoleAndLogsOutput() {
        return new VerbosePlugin(true, true);
    }
}