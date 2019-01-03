package com.baozi.bootstrap;

import com.baozi.handler.HttpRequestHandler;
import com.baozi.handler.TextWebSocketFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 默认初始管道构造器
 * @Author: baozi
 * @Create: 2018-09-25 10:53
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final int MAX_HTTP_CONTEXT_LENGTH = 64 * 1024;
    private static final long ALL_TIME_OUT = 60L;

    @Override
    protected void initChannel(SocketChannel socketChannel){
        socketChannel.pipeline().addLast(new IdleStateHandler(0,
                    0, ALL_TIME_OUT, TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(new HttpServerCodec());
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
        socketChannel.pipeline().addLast(new HttpObjectAggregator(MAX_HTTP_CONTEXT_LENGTH));
        socketChannel.pipeline().addLast(new HttpRequestHandler());
        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/"));
        socketChannel.pipeline().addLast(new TextWebSocketFrameHandler());
    }
}
