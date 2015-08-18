package com.tormenta.proxy.streaming;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import java.util.logging.Logger;

public class StreamingProxyClientHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = Logger.getLogger(StreamingProxyChangeRequestHandler.class.getName());

    private final Channel proxyServerChannel;

    private EmbeddedChannel decoder = new EmbeddedChannel(new HttpResponseDecoder());
    private EmbeddedChannel encoder = new EmbeddedChannel(new HttpResponseEncoder());


    public StreamingProxyClientHandler(Channel proxyServerChannel){
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        logger.info("channelActive");
        ctx.read();
        ctx.write(Unpooled.EMPTY_BUFFER);
    }


    CompositeByteBuf response = Unpooled.compositeBuffer();
    HttpResponse header = null;
    Integer currentLen = 0;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception{

        if( header == null ) {
            String data = ((ByteBuf)msg).duplicate().toString(CharsetUtil.UTF_8);
            String head = data.split("\r\n\r\n")[0];
            Integer headerLen = head.length() + 4;

            decoder.writeInbound(msg);
            header = (HttpResponse) decoder.readInbound();

            currentLen = ((ByteBuf) msg).writerIndex() - headerLen;
            byte[] bytes = new byte[currentLen];
            ((ByteBuf)msg).getBytes(headerLen, bytes);
            response.addComponent(Unpooled.copiedBuffer(bytes));
            ctx.channel().read();

        } else {
            currentLen += ((ByteBuf) msg).writerIndex();

            response.addComponent((ByteBuf) msg);

            if (currentLen == Integer.parseInt(header.headers().get("Content-Length"))) {

                header.headers().set("Content-Length", currentLen);
                CompositeByteBuf finalResponse = Unpooled.compositeBuffer();

                encoder.writeOutbound(header);
                ByteBuf encoded = (ByteBuf) encoder.readOutbound();

                finalResponse.addComponent(encoded);
                finalResponse.addComponent(response.consolidate().component(0));
                ByteBuf bb = finalResponse.consolidate().component(0);

                proxyServerChannel.writeAndFlush(bb).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) {
                        if (channelFuture.isSuccess()) {
                            logger.info("Success");
                            ctx.channel().read();
                        } else {
                            logger.info("Failure");
                            channelFuture.channel().close();
                        }
                    }
                });
            } else {
                ctx.channel().read();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        logger.info("channelInactive");
        StreamingProxyServerHandler.closeOnFlush(proxyServerChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.info("exceptionCaught");
        cause.printStackTrace();
        StreamingProxyServerHandler.closeOnFlush(ctx.channel());
    }
}
