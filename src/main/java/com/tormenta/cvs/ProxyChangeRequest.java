package com.tormenta.cvs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.logging.Logger;

public class ProxyChangeRequest extends ChannelInboundHandlerAdapter {

    private final static Logger logger = Logger.getLogger(ProxyChangeRequest.class.getName());
    private final String remoteHost;
    private final int remotePort;

    private EmbeddedChannel encoder = new EmbeddedChannel(new HttpRequestEncoder());
    private EmbeddedChannel decoder = new EmbeddedChannel(new HttpRequestDecoder());


    public ProxyChangeRequest(String remoteHost, int remotePort){
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
        decoder.writeInbound(msg);
        HttpObject decoded = (HttpRequest)decoder.readInbound();
        logger.info(decoded.toString());

        logger.info("channelRead0");
        if (decoded instanceof HttpRequest) {
            HttpRequest httpr = (HttpRequest) decoded;
            httpr.headers().set("Host", getHostString(remoteHost, remotePort));
            logger.info(httpr.toString());
            decoded = httpr;
        }
        encoder.writeOutbound(decoded);
        ByteBuf encoded = (ByteBuf)encoder.readOutbound();
        logger.info(encoded.toString(CharsetUtil.UTF_8));
        ctx.fireChannelRead(encoded);
    }
}
