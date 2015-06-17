package com.tormenta.multitest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MultiTest {

    public static void main(String[] args) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup(1);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MultiTestInitializer())
                    .bind(9090).sync().channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
        }
    }
}
