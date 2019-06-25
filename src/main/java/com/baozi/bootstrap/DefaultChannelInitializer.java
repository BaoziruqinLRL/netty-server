package com.baozi.bootstrap;

import com.baozi.constructor.ServerConstructor;
import com.baozi.handler.HttpRequestHandler;
import com.baozi.handler.TextWebSocketFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 默认初始管道构造器
 * @Author: lirl
 * @Create: 2018-09-25 10:53
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final int MAX_HTTP_CONTEXT_LENGTH = 64 * 1024;
    private static final long ALL_TIME_OUT = 30L;

    /**
     * 接收心跳消息类型
     */
    private String heartbeatType;

    /**
     * 回复心跳消息类型
     */
    private String heartbeatReplyType;

    /**
     * 是否开启服务端主动心跳
     */
    private boolean serverHeartbeat;

    DefaultChannelInitializer(String heartbeatType, String heartbeatReplyType, boolean serverHeartbeat) {
        this.heartbeatType = heartbeatType;
        this.heartbeatReplyType = heartbeatReplyType;
        this.serverHeartbeat = serverHeartbeat;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel){
        if (ServerConstructor.getSsl()) {
            socketChannel.pipeline().addLast(new SslHandler(buildSslEngine()));
        }
        socketChannel.pipeline().addLast(new IdleStateHandler(0,
                0, ALL_TIME_OUT, TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(new HttpServerCodec());
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
        socketChannel.pipeline().addLast(new HttpObjectAggregator(MAX_HTTP_CONTEXT_LENGTH));
        socketChannel.pipeline().addLast(new HttpRequestHandler());
        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/"));
        socketChannel.pipeline().addLast(new TextWebSocketFrameHandler(heartbeatType,heartbeatReplyType,serverHeartbeat));
    }

    private SSLEngine buildSslEngine(){
        var type = ServerConstructor.getSslKeyType();
        var path = ServerConstructor.getSslKeyPath();
        var password = ServerConstructor.getSslKeyPwd();
        try {
            var ks = KeyStore.getInstance(type);
            var ksInputStream = new FileInputStream(path);
            ks.load(ksInputStream, password.toCharArray());
            //KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
            //getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
            var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password.toCharArray());

            //SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
            var sslContext = SSLContext.getInstance("SSL");
            sslContext.init(kmf.getKeyManagers(), null, null);
            var engine = sslContext.createSSLEngine();
            engine.setUseClientMode(false);
            return engine;
        }catch (Exception ex){
            throw new RuntimeException("build sslEngine exception.",ex);
        }
    }
}
