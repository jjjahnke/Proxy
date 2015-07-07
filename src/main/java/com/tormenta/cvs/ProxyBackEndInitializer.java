package com.tormenta.cvs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ProxyBackEndInitializer extends ChannelInitializer<SocketChannel> {

    private final Channel inboundChannel;

    public ProxyBackEndInitializer(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline()
//                .addLast(new HttpClientCodec())
//                .addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
                .addLast(new ProxyBackEndHandler(inboundChannel));
    }
}
