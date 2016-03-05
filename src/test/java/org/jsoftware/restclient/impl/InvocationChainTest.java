package org.jsoftware.restclient.impl;

import org.jsoftware.restclient.RestClientPlugin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class InvocationChainTest {
    private PluginContextImpl ctx;
    private List<String> actions;

    @Before
    public void setUp() throws Exception {
        actions = new LinkedList<>();
        ctx = new PluginContextImpl();
    }

    @Test
    public void testChain() throws Exception {
        final RestClientPlugin plugin0 = new InvocationChainPlugin("p0");
        final RestClientPlugin plugin1 = new InvocationChainPlugin("p1");
        RestClientPlugin[] plugins = new RestClientPlugin[] { plugin0, plugin1 };
        InvocationChain invocationChain = InvocationChain.create(plugins, ctx, () -> {
            actions.add("action");
            return null;
        });
        invocationChain.continueChain();
        Assert.assertEquals("p0 before", actions.get(0));
        Assert.assertEquals("p1 before", actions.get(1));
        Assert.assertEquals("action", actions.get(2));
        Assert.assertEquals("p1 after", actions.get(3));
        Assert.assertEquals("p0 after", actions.get(4));
        Assert.assertEquals(5, actions.size());
    }


    class InvocationChainPlugin implements RestClientPlugin {
        private final String name;

        InvocationChainPlugin(String name) {
            this.name = name;
        }

        @Override
        public void plugin(PluginContext context, PluginChain chain) throws Exception {
            Assert.assertSame(InvocationChainTest.this.ctx, context);
            actions.add(name + " before");
            chain.continueChain();
            actions.add(name + " after");
        }
    }
}

