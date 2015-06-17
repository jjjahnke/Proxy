package com.tormenta.multitest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiTestHandler2 extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(MultiTestHandler2.class.getName());
    private static final String name = MultiTestHandler2.class.getSimpleName();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("channelActive");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead");
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, cause.getMessage(), cause);
        ctx.close();
    }
}