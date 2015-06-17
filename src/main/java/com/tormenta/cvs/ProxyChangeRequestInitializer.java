package com.tormenta.cvs;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProxyChangeRequestInitializer extends ChannelInitializer<SocketChannel> {

    private final String remoteHost;
    private final int remotePort;

    public ProxyChangeRequestInitializer(String remoteHost, int remotePort ) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void initChannel(SocketChannel ch){
        ChannelPipeline pipe = ch.pipeline();

        pipe.addLast("logger", new LoggingHandler(LogLevel.INFO));
        //pipe.addLast("codec", new HttpServerCodec());
        pipe.addLast("proxy_change_handler", new ProxyChangeRequest(this.remoteHost, this.remotePort));
        pipe.addLast("front_end_handler",new ProxyFrontEndHandler(remoteHost, remotePort));

    }
}