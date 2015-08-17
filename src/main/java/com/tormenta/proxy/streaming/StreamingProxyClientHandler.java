package com.tormenta.proxy.streaming;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.util.logging.Logger;

public class StreamingProxyClientHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = Logger.getLogger(StreamingProxyChangeRequestHandler.class.getName());

    private final Channel proxyServerChannel;

    public StreamingProxyClientHandler(Channel proxyServerChannel){
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        logger.info("channelActive");
        ctx.read();
        ctx.write(Unpooled.EMPTY_BUFFER);
    }


    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception{
        proxyServerChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    logger.info("Success");
                    ctx.channel().read();
                } else {
                    logger.info("Failure");
                    channelFuture.channel().close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        logger.info("channelInactive");
        StreamingProxyServerHandler.closeOnFlush(proxyServerChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.info("exceptionCaught");
        cause.printStackTrace();
        StreamingProxyServerHandler.closeOnFlush(ctx.channel());
    }
}
