package com.tormenta.proxy.streaming;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

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
        //pipeline.addLast(new LoggingHandler(LogLevel.WARN));
        pipeline.addLast(new StreamingProxyClientHandler(proxyServerChannel));
    }
}
