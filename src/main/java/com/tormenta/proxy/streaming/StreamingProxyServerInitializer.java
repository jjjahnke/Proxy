package com.tormenta.proxy.streaming;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class StreamingProxyServerInitializer extends ChannelInitializer<SocketChannel> {
    private final String remoteHost;
    private final int remotePort;

    public StreamingProxyServerInitializer(String remoteHost, int remotePort){
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.INFO),
                new StreamingProxyChangeRequestHandler(remoteHost, remotePort),
                new StreamingProxyServerHandler(remoteHost, remotePort));
    }
}
