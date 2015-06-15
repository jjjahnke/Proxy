package com.tormenta.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf>{

    private final String msg;
    final BlockingQueue<String> answer = new LinkedBlockingQueue<String>();

    public EchoClientHandler(String msg){ this.msg = msg; }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer(this.msg, CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        String msg_string = in.toString(CharsetUtil.UTF_8);
        System.out.println("Client received: " + msg_string);
        answer.add(msg_string);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
