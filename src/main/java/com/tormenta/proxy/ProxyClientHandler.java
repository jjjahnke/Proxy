package com.tormenta.proxy;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpObject;

public class ProxyClientHandler extends SimpleChannelInboundHandler<HttpObject>{

    private final Channel proxyServerChannel;

    public ProxyClientHandler(Channel proxyServerChannel){
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){

        System.out.println("**************************************In Channel Active");
        ctx.read();
        ctx.write(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, HttpObject msg) {
        System.out.println("**************************************In Channel Read");
        proxyServerChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    ctx.channel().read();
                } else {
                    channelFuture.channel().close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        ProxyServerHandler.closeOnFlush(proxyServerChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ProxyServerHandler.closeOnFlush(ctx.channel());
    }
}
