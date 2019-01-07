package com.baozi.handler;

import com.baozi.business.BusinessWorker;
import com.baozi.cache.ChannelCache;
import com.baozi.constant.ExecutorConstant;
import com.baozi.constructor.ServerConstructor;
import com.baozi.data.ChannelParam;
import com.baozi.data.TransferData;
import com.baozi.retry.RetryCache;
import com.baozi.util.KeyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelOutputShutdownEvent;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: websocket消息处理器
 * @Author: baozi
 * @Create: 2018-09-25 11:00
 */
public class TextWebSocketFrameHandler extends ChannelInboundHandlerAdapter {

    /**
     * 丢失3次心跳则为丢失连接
     */
    private static final int LOST_CONNECT = 3;

    /**
     * 心跳计数器
     */
    private AtomicInteger heartbeatCount = new AtomicInteger(0);

    /**
     * 初始化传入的心跳消息type
     */
    private String heartbeatType = ServerConstructor.getHeartbeatType();

    /**
     * 初始化设置的心跳消息回复type
     */
    private String heartbeatReplyType = ServerConstructor.getHeartbeatReply();

    /**
     * 服务器主动心跳
     */
    private boolean serverHeartbeat = Optional.of(ServerConstructor.isServerHeartbeat()).orElse(false);

    private static final Logger log = LoggerFactory.getLogger(TextWebSocketFrame.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            // 移除http处理器
            ctx.pipeline().remove(HttpRequestHandler.class);
        }else if (evt instanceof IdleStateEvent){
            // 心跳检测事件
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE){
                if (heartbeatCount.get() == LOST_CONNECT) {
                    // 丢失连接，关闭channel，清除缓存
                    ctx.close().sync();
                    ChannelParam param = ChannelCache.removeChannelByChannelId(ctx.channel().id());
                    log.warn(MessageFormat.format("Client {0} lost connection...", (param == null) ? null : param.getClientId()));
                    // 通知失联回调
                    TransferData transferData = new TransferData();
                    transferData.setType(ExecutorConstant.LOST_CONNECT_KEY);
                    transferData.setContent((param == null)?null:param.getClientId());
                    BusinessWorker.distributeTask(transferData,ctx.channel());
                }else{
                    // 如果设置了需要服务器主动心跳，则会主动发起心跳消息
                    if (serverHeartbeat) {
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(heartbeatType));
                    }
                    heartbeatCount.incrementAndGet();
                }
            }
        }else if (evt instanceof ChannelInputShutdownEvent || evt instanceof ChannelOutputShutdownEvent){
            ctx.close().sync();
            ChannelCache.removeChannelByChannelId(ctx.channel().id());
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        if (msg instanceof TextWebSocketFrame) {
            try {
                TransferData transferData = ServerConstructor.getBusinessMessageDecode().decode((TextWebSocketFrame) msg);
                if (heartbeatReply(transferData, channelHandlerContext)) {
                    // 收到客户端心跳，心跳计数器清0
                    heartbeatCount.set(0);
                } else if (ackReply(transferData)){
                    // 收到消息ack回复,从时间片轮转中移除数据
                    if (RetryCache.isWheel()){
                        RetryCache.timeWheelProcessor().removeFromWheel(transferData.getAck());
                    }
                } else{
                    BusinessWorker.distributeTask(transferData, channelHandlerContext.channel());
                }
            }catch (Exception e){
                log.error("channel read exception {}",e.getMessage());
            } finally{
                ReferenceCountUtil.release(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        // channel异常，关闭channel，清除缓存
        ChannelParam param = ChannelCache.removeChannelByChannelId(ctx.channel().id());
        param.getChannel().close().sync();
        log.error("Client {} Channel error. Close channel...Exception is {}",param.getClientId(),cause);
        // 通知失联回调
        TransferData transferData = new TransferData();
        transferData.setType(ExecutorConstant.LOST_CONNECT_KEY);
        transferData.setContent(param.getClientId());
        BusinessWorker.distributeTask(transferData,ctx.channel());
    }

    /**
     * 判断心跳，若是客户端的主动心跳，则回复心跳响应；若是客户端的心跳响应则不回复
     * @param transferData 消息
     * @return 返回判断结果
     */
    private boolean heartbeatReply(TransferData transferData, ChannelHandlerContext channelHandlerContext) {
        if (heartbeatType.equals(transferData.getType())) {
            channelHandlerContext.writeAndFlush(new TextWebSocketFrame(heartbeatReplyType));
            return true;
        }else if (heartbeatReplyType.equals(transferData.getType())){
            return true;
        }else{
            return false;
        }
    }

    private boolean ackReply(TransferData transferData){
        if (transferData != null && transferData.getAck() != null && transferData.getAck().startsWith(KeyUtil.ACK_KEY)){
            return true;
        }else{
            return false;
        }
    }
}
