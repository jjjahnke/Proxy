package com.tormenta.cvs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;

import java.util.logging.Logger;

public class ProxyChangeRequestHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = Logger.getLogger(ProxyChangeRequestHandler.class.getName());
    private final String remoteHost;
    private final int remotePort;

    private EmbeddedChannel encoder = new EmbeddedChannel(new HttpRequestEncoder());
    private EmbeddedChannel decoder = new EmbeddedChannel(new HttpRequestDecoder());


    public ProxyChangeRequestHandler(String remoteHost, int remotePort){
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public String getHostString(String host, int port){
        if (port == 80){
            return host;
        } else {
            return host + ":" + port;
        }
    }

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) {
//        logger.info("channelActive");
//        ctx.fireChannelActive();
//        //ctx.read();
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("channelRead");
        if(msg instanceof HttpObject) {

        } else {
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
}
