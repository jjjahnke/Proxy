package com.tormenta.multitest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiTestHandler1 extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(MultiTestHandler1.class.getName());
    private static final String name = MultiTestHandler1.class.getSimpleName();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("channelActive");
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead");
        String s = ((ByteBuf)msg).toString(CharsetUtil.UTF_8);
        ByteBuf rs = Unpooled.copiedBuffer(new StringBuilder(s).reverse().toString(), CharsetUtil.UTF_8);
        ctx.fireChannelRead(rs);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("channelReadComplete");
        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, cause.getMessage(), cause);
        ctx.close();
    }
}