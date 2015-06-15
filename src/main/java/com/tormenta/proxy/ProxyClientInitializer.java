package com.tormenta.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class ProxyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final Channel proxyServerChannel;

    public ProxyClientInitializer(Channel proxyServerChannel) {
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("codec", new HttpClientCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(512*1024));
        pipeline.addLast("handler", new ProxyClientHandler(proxyServerChannel));
    }
}
