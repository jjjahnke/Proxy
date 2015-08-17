package com.tormenta.proxy.streaming;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;

import java.util.logging.Logger;

public class StreamingProxyChangeRequestHandler extends ChannelDuplexHandler {
    private final static Logger logger = Logger.getLogger(StreamingProxyChangeRequestHandler.class.getName());
    private final String remoteHost;
    private final int remotePort;

    private EmbeddedChannel encoder = new EmbeddedChannel(new HttpRequestEncoder());
    private EmbeddedChannel decoder = new EmbeddedChannel(new HttpRequestDecoder());
    private EmbeddedChannel endecoder = new EmbeddedChannel(new HttpClientCodec(), new HttpObjectAggregator(Integer.MAX_VALUE));

    private CompositeByteBuf resBuf = Unpooled.compositeBuffer();


    public StreamingProxyChangeRequestHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public String getHostString(String host, int port) {
        if (port == 80) {
            return host;
        } else {
            return host + ":" + port;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead");
        decoder.writeInbound(msg);
        HttpObject decoded = (HttpRequest) decoder.readInbound();

        if (decoded instanceof HttpRequest) {
            HttpRequest httpr = (HttpRequest) decoded;
            httpr.headers().set("Host", getHostString(remoteHost, remotePort));
            decoded = httpr;
        }
        encoder.writeOutbound(decoded);
        ByteBuf encoded = (ByteBuf) encoder.readOutbound();
        ctx.fireChannelRead(encoded);
    }

}
