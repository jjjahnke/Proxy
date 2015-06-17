package com.tormenta.cvs;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.util.logging.Logger;

/**
 * Created by jeromejahnke on 6/16/15.
 */
public class ProxyBackEndHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ProxyBackEndHandler.class.getName());

    private final Channel inboundChannel;

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
        inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            }
        });
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