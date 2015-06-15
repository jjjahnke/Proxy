package com.tormenta.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

public class ProxyServerHandler extends ChannelInboundHandlerAdapter{
    private final String remoteHost;
    private final int remotePort;

    private volatile Channel proxyClientChannel;

    public ProxyServerHandler(String remoteHost, int remotePort){
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel proxyServerChannel = ctx.channel();

        Bootstrap b = new Bootstrap();
        b.group(proxyServerChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ProxyClientInitializer(proxyServerChannel))
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(remoteHost, remotePort);
        proxyClientChannel = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
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
        if (proxyClientChannel.isActive()) {
            proxyClientChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    if(channelFuture.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        channelFuture.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        if (proxyClientChannel != null ) {
            closeOnFlush(proxyClientChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    static void closeOnFlush(Channel ch) {
        if(ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
