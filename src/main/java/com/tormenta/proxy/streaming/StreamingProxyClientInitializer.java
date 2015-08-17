package com.tormenta.proxy.streaming;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class StreamingProxyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final Channel proxyServerChannel;

    public StreamingProxyClientInitializer(Channel proxyServerChannel) {
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast("codec", new HttpClientCodec());
//        pipeline.addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast("handler", new StreamingProxyClientHandler(proxyServerChannel));
    }
}
