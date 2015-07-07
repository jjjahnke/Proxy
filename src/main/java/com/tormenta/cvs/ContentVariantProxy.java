package com.tormenta.cvs;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ContentVariantProxy {
    public static void main(String[] args) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ProxyChangeRequestHandlerInitializer("tormenta.com", 80))
                    //.childHandler(new ProxyFrontEndInitializer("tormenta.com", 80))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(9090).sync().channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
        }
    }

}
