package com.tormenta.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import junit.framework.Assert;
import org.junit.Test;

public class EchoClientHandlerTest {

    @Test
    public void testChannelActive() throws Exception {
        String msg = "Test Message";
        EmbeddedChannel channel = new EmbeddedChannel(new EchoClientHandler(msg));

        ByteBuf out = (ByteBuf)channel.readOutbound();
        String out_msg = out.toString(CharsetUtil.UTF_8);
        Assert.assertEquals(msg, out_msg);

    }

    @Test
    public void testChannelRead0() throws Exception {
        String msg = "Test Message";
        String test = "Test String";
        EmbeddedChannel channel = new EmbeddedChannel(new EchoClientHandler(msg));

        ByteBuf buf = Unpooled.copiedBuffer(test, CharsetUtil.UTF_8);
        ByteBuf input = buf.duplicate();
        Assert.assertFalse(channel.writeInbound(input));

        EchoClientHandler ech = channel.pipeline().get(EchoClientHandler.class);
        Assert.assertEquals(test, ech.answer.take());
    }
}