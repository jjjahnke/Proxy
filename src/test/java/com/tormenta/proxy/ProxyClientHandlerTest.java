package com.tormenta.proxy;

import com.tormenta.proxy.streaming.StreamingProxyClientHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import junit.framework.Assert;
import org.junit.Test;


public class ProxyClientHandlerTest {
    public class TestHandler extends ChannelInboundHandlerAdapter {

        public Channel channel;

        @Override
        public void channelActive( ChannelHandlerContext ctx) {
            this.channel = ctx.channel();
        }

    }

    @Test
    public void testChannelRead0_default() throws Exception {
        EmbeddedChannel proxyServerChannel = new EmbeddedChannel(new TestHandler());
        TestHandler psChannel = proxyServerChannel.pipeline().get(TestHandler.class);
        EmbeddedChannel proxyClientChannel = new EmbeddedChannel(new StreamingProxyClientHandler(psChannel.channel));

        FullHttpResponse rs = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));
        rs.headers().set("Content-Type", "text/plain; charset=utf-8");

        proxyClientChannel.writeInbound(rs);
        Assert.assertEquals(rs, proxyServerChannel.readOutbound());
    }
}