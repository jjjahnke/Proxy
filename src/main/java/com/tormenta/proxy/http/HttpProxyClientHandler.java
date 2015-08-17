package com.tormenta.proxy.http;

import com.tormenta.proxy.streaming.StreamingProxyChangeRequestHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;

import java.util.logging.Logger;

public class HttpProxyClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final static Logger logger = Logger.getLogger(StreamingProxyChangeRequestHandler.class.getName());

    private final Channel proxyServerChannel;

    public HttpProxyClientHandler(Channel proxyServerChannel){
        this.proxyServerChannel = proxyServerChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        logger.info("channelActive");
        ctx.read();
        ctx.write(Unpooled.EMPTY_BUFFER);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        logger.info("channelRead0");
        proxyServerChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
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

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("channelRead");
        if (acceptInboundMessage(msg)) {
            FullHttpResponse imsg = (FullHttpResponse) msg;
            channelRead0(ctx, imsg);
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        logger.info("channelInactive");
        HttpProxyServerHandler.closeOnFlush(proxyServerChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.info("exceptionCaught");
        cause.printStackTrace();
        HttpProxyServerHandler.closeOnFlush(ctx.channel());
    }
}
