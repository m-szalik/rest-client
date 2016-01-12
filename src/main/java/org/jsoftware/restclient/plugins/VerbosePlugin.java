package org.jsoftware.restclient.plugins;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoftware.restclient.RestClientPlugin;
import org.jsoftware.restclient.RestClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Display request and response content.
 * <p>Use static factory method to obtain an instance.</p>
 * @author szalik
 */
public class VerbosePlugin implements RestClientPlugin {
    public enum RenderingOption {
        REQUEST_URL {
            @Override
            void output(StringBuilder buff, PluginContext ctx, Object arg) throws IOException {
                HttpRequestBase request = ctx.getRequest();
                buff.append("> ").append(request.getMethod()).append(' ').append(ctx.getURI()).append('\n');
            }
        },
        REQUEST_BODY {
            @Override
            void output(StringBuilder buff, PluginContext ctx, Object arg) throws IOException {
                HttpRequestBase request = ctx.getRequest();
                if (request instanceof HttpEntityEnclosingRequestBase) {
                    HttpEntityEnclosingRequestBase r = (HttpEntityEnclosingRequestBase) request;
                    HttpEntity entity = r.getEntity();
                    if (entity != null) {
                        appendBody(buff, "> ", IOUtils.toString(entity.getContent()));
                    }
                }
            }
        },
        REQUEST_HEADERS {
            @Override
            void output(StringBuilder buff, PluginContext ctx, Object arg) throws IOException {
                appendHeaders(buff, "< (header) ", ctx.getRequest().getAllHeaders());
            }
        },
        RESPONSE_STATUS {
            @Override
            void output(StringBuilder buff, PluginContext ctx, Object arg) throws IOException {
                if (ctx.isResponseAvailable()) {
                    RestClientResponse response = ctx.getResponse();
                    buff.append("< ").append(response.getStatusLine()).append('\n');
                }
            }
        },
        RESPONSE_HEADERS {
            @Override
            void output(StringBuilder buff, PluginContext ctx, Object arg) throws IOException {
                if (ctx.isResponseAvailable()) {
                    appendHeaders(buff, "< (header) ", ctx.getResponse().getAllHeaders());
                }
            }
        },
        RESPONSE_BODY {
            @Override
            void output(StringBuilder buff, PluginContext ctx, Object arg) throws IOException {
                if (ctx.isResponseAvailable()) {
                    appendBody(buff, "< ", ctx.getResponse().getContent());
                }
            }
        },
        RESPONSE_TIME {
            @Override
            void output(StringBuilder buff, PluginContext ctx, Object arg) throws IOException {
                buff.append("* Time: ").append(arg).append(" ms.\n");
            }
        },

        ;
        abstract void output(StringBuilder buff, PluginContext ctx, Object arg) throws IOException;

        private static void appendBody(StringBuilder buff, String prefix, String content) {
            if (StringUtils.isNotBlank(content)) {
                for(String s : StringUtils.split(content, "\n")) {
                    buff.append(prefix).append(s).append('\n');
                }
            }
        }

        private static void appendHeaders(StringBuilder buff, String prefix, Header[] headers) {
            if (headers != null && headers.length > 0) {
                for(Header header : headers) {
                    buff.append(prefix).append(header.getName()).append(": ").append(header.getValue()).append('\n');
                }
            }
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PrintStream[] output;
    private final boolean logsOutput;
    private final Set<RenderingOption> options = new HashSet<>();

    /**
     * Create plugin instance with all possible rendering options
     * @param logsOutput it true output goes to slf4j logs
     * @param outputs array of PrintStreams to send output there
     */
    public VerbosePlugin(boolean logsOutput, PrintStream[] outputs) {
        this(logsOutput, outputs, RenderingOption.values());
    }

    /**
     * Create plugin instance
     * @param logsOutput it true output goes to slf4j logs
     * @param outputs array of PrintStreams to send output there
     * @param options rendering options
     */
    public VerbosePlugin(boolean logsOutput, PrintStream[] outputs, RenderingOption... options) {
        this.output = outputs == null ? new PrintStream[0] : outputs;
        this.logsOutput = logsOutput;
        for(RenderingOption oo : options == null ? RenderingOption.values() : options) {
            this.options.add(oo);
        }
        logger.debug("Creating {} with logsOutput={} and {}", getClass(), logsOutput, StringUtils.join(this.output, ','));

    }

    @Override
    public void plugin(PluginContext context, PluginChain chain) throws Exception {
        StringBuilder buff = new StringBuilder();
        long startTs = System.currentTimeMillis();
        boolean error = true;
        try {
            append(buff, context, RenderingOption.REQUEST_URL, null);
            append(buff, context, RenderingOption.REQUEST_HEADERS, null);
            append(buff, context, RenderingOption.REQUEST_BODY, null);
            chain.continueChain();
            RestClientResponse response = context.getResponse();
            int code = response.getStatusLine().getStatusCode();
            error = code < 200 || code >= 400;
        } catch (Exception ex) {
            buff.append("* ").append(ex);
            throw ex;
        } finally {
            long time = System.currentTimeMillis() - startTs;
            append(buff, context, RenderingOption.RESPONSE_STATUS, null);
            append(buff, context, RenderingOption.RESPONSE_HEADERS, null);
            append(buff, context, RenderingOption.RESPONSE_BODY, null);
            append(buff, context, RenderingOption.RESPONSE_TIME, time);
            print(buff, error);
        }
    }

    private void append(StringBuilder buff, PluginContext ctx, RenderingOption option, Object arg) throws IOException {
        if (this.options.contains(option)) {
            option.output(buff, ctx, arg);
        }
    }

    private void print(StringBuilder output, boolean error) {
        for (PrintStream ps : this.output) {
            ps.println(output.toString());
            ps.flush();
        }
        if (logsOutput) {
            for(String line : output.toString().split("\n")) {
                if (error) {
                    logger.warn(line);
                } else {
                    logger.info(line);
                }
            }
        }
    }


}
