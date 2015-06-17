package com.tormenta.multitest;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by jeromejahnke on 6/16/15.
 */
public class MultiTestInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    public void initChannel(SocketChannel ch){
        ChannelPipeline pipe = ch.pipeline();

        pipe.addLast("logger", new LoggingHandler(LogLevel.INFO));
        pipe.addLast("handler1", new MultiTestHandler1());
        pipe.addLast("handler2", new MultiTestHandler2());
    }
}
