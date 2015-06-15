package com.tormenta.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Test;

public class EchoServerHandlerTest {
    @Test
    public void testChannelRead() throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("Test String", CharsetUtil.UTF_8);

        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new EchoServerHandler());

        Assert.assertFalse(channel.writeInbound(input));
        Assert.assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readOutbound();
        Assert.assertEquals(buf, read);
        read.release();
        Assert.assertNull(channel.readInbound());

    }
}