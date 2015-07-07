package com.tormenta.cvs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;

import java.util.logging.Logger;

public class ProxyChangeRequestHandler extends ChannelDuplexHandler {

    private final static Logger logger = Logger.getLogger(ProxyChangeRequestHandler.class.getName());
    private final String remoteHost;
    private final int remotePort;

    private EmbeddedChannel requestEncoder = new EmbeddedChannel(new HttpRequestEncoder());
    private EmbeddedChannel requestDecoder = new EmbeddedChannel(new HttpRequestDecoder());

    private EmbeddedChannel responseEncoder = new EmbeddedChannel(new HttpResponseEncoder());
    private EmbeddedChannel responseDecoder = new EmbeddedChannel(new HttpResponseDecoder());

    private CompositeByteBuf writeCollector = Unpooled.compositeBuffer();

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
            logger.info("HttpObject Leg");
            if (msg instanceof HttpRequest) {
                HttpRequest httpr = (HttpRequest) msg;
                httpr.headers().set("Host", getHostString(remoteHost, remotePort));
                msg = httpr;
            }
            ctx.fireChannelRead(msg);
        } else {
            logger.info("ByteBuf Leg");
            requestDecoder.writeInbound(msg);
            HttpObject decoded = (HttpRequest) requestDecoder.readInbound();
            if (decoded instanceof HttpRequest) {
                HttpRequest httpr = (HttpRequest) decoded;
                httpr.headers().set("Host", getHostString(remoteHost, remotePort));
                decoded = httpr;
            }
            requestEncoder.writeOutbound(decoded);
            ByteBuf encoded = (ByteBuf) requestEncoder.readOutbound();
            ctx.fireChannelRead(encoded);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("write");
//        responseDecoder.writeInbound(msg);
//        HttpObject decoded = (HttpResponse) responseDecoder.readInbound();
//        String str = decoded.toString();
//        logger.info(str);
//        responseEncoder.writeOutbound(decoded);
//        ByteBuf encoded = (ByteBuf) responseEncoder.readOutbound();

        writeCollector.addComponent((ByteBuf)msg);
        promise.setSuccess();

        //ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        logger.info("flush");
        writeCollector.consolidate();
        ctx.writeAndFlush(writeCollector.component(0));

        //ctx.flush();
    }

}
