package com.tormenta.proxy.streaming;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;

import java.util.logging.Logger;

public class StreamingProxyServerHandler extends ChannelInboundHandlerAdapter{
    private final static Logger logger = Logger.getLogger(StreamingProxyChangeRequestHandler.class.getName());

    private final String remoteHost;
    private final int remotePort;

    private volatile Channel proxyClientChannel;

    private EmbeddedChannel encoder = new EmbeddedChannel(new HttpRequestEncoder());
    private EmbeddedChannel decoder = new EmbeddedChannel(new HttpRequestDecoder());

    public StreamingProxyServerHandler(String remoteHost, int remotePort){
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("channelActive");

        final Channel proxyServerChannel = ctx.channel();

        Bootstrap b = new Bootstrap();
        b.group(proxyServerChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new StreamingProxyClientInitializer(proxyServerChannel))
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(remoteHost, remotePort);
        proxyClientChannel = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                logger.info("Callback Listener");

                if(channelFuture.isSuccess()){
                    proxyServerChannel.read();
                } else {
                    proxyServerChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead");

        if (proxyClientChannel.isActive()) {
            proxyClientChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    if(channelFuture.isSuccess()) {
                        logger.info("Success");
                        ctx.channel().read();
                    } else {
                        logger.info("Failure");
                        channelFuture.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        logger.info("channelInactive");
        if (proxyClientChannel != null ) {
            closeOnFlush(proxyClientChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("exceptionCaught");
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    static void closeOnFlush(Channel ch) {
        logger.info("closeOnFlush");
        if(ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
