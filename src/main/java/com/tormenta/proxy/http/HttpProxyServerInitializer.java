package com.tormenta.proxy.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpProxyServerInitializer extends ChannelInitializer<SocketChannel> {
    private final String remoteHost;
    private final int remotePort;

    public HttpProxyServerInitializer(String remoteHost, int remotePort){
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.INFO),
                new HttpServerCodec(),
                new HttpObjectAggregator(Integer.MAX_VALUE),
                new HttpProxyServerHandler(remoteHost, remotePort));
    }
}
