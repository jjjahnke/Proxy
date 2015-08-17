package com.tormenta.proxy.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpProxyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final Channel proxyServerChannel;

    public HttpProxyClientInitializer(Channel proxyServerChannel) {
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("codec", new HttpClientCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast("handler", new HttpProxyClientHandler(proxyServerChannel));
    }
}
