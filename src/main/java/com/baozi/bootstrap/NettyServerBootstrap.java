package com.baozi.bootstrap;

import com.baozi.constructor.ServerConstructor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Description: 服务器启动引导类
 * @Author: lirl
 * @Create: 2018-09-25 11:04
 */
@Slf4j
public class NettyServerBootstrap {

    private int port;

    public NettyServerBootstrap(int port) {
        this.port = port;
    }

    public void start(String heartbeatType,String heartbeatReplyType,boolean serverHeartbeat){
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new DefaultChannelInitializer(heartbeatType,heartbeatReplyType,serverHeartbeat))
                    // 关闭Nagle算法，降低数据延迟
                    .option(ChannelOption.TCP_NODELAY,true)
                    // 关闭tcp自动检测，由心跳事件实现
                    .option(ChannelOption.SO_KEEPALIVE,false)
                    // 自动增长收缩接收容器
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                    // 重用缓存区
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    // 不阻塞close调用
                    .option(ChannelOption.SO_LINGER, 0);
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            ChannelFuture cf = bootstrap.bind().sync();
            cf.channel().closeFuture().sync();
        }catch (Exception ex){
            // 日志打印
        } finally {
            try {
                group.shutdownGracefully().sync();
            }catch (Exception ex){
                // 日志打印
                log.error("netty bootstrap {}", ex);
            }
        }
    }
}
