package com.tormenta.cvs;

import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;

import java.util.logging.Logger;

/**
 * Created by jeromejahnke on 6/16/15.
 */
public class ProxyBackEndHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ProxyBackEndHandler.class.getName());

    private final Channel inboundChannel;

    private static Integer count = 0;

    private EmbeddedChannel encoder = new EmbeddedChannel(new HttpRequestEncoder());
    private EmbeddedChannel decoder = new EmbeddedChannel(new HttpRequestDecoder());

    private CompositeByteBuf responseBuf = Unpooled.compositeBuffer();

    public ProxyBackEndHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("channelActive");
        ctx.read();
        ctx.write(Unpooled.EMPTY_BUFFER);

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        count++;
        final Integer curCount = new Integer(count);
        logger.info("channelRead [" + curCount + "]");
        inboundChannel.write(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                logger.info("ReadCallback [" + curCount + "]");
                logger.info("isActive() [" + ctx.channel().isActive() + "]" );
                logger.info("isOpen() [" + ctx.channel().isOpen() + "]" );
                logger.info("isWritable() [" + ctx.channel().isWritable() + "]" );
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("channelReadComplete");
        logger.info("isActive() [" + ctx.channel().isActive() + "]" );
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ProxyFrontEndHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ProxyFrontEndHandler.closeOnFlush(ctx.channel());
    }
}