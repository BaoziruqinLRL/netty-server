package com.baozi.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

/**
 * @Description: http请求消息处理器
 * @Author: baozi
 * @Create: 2018-09-25 10:58
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String HEADER_UPGRADE_KEY = "Upgrade";
    private static final String HEADER_UPGRADE_VALUE = "websocket";
    private static final String HEADER_WEBSOCKET_KEY = "Sec-WebSocket-Version";
    private static final String HEADER_WEBSOCKET_VERSION = "13";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        // 只处理websocket请求，若不是websocket请求，则关闭通道
        if (canUpgrade(fullHttpRequest)){
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
        }else{
            HttpResponse response = new DefaultHttpResponse(fullHttpRequest.protocolVersion(), HttpResponseStatus.NOT_FOUND);
            channelHandlerContext.writeAndFlush(response);
            channelHandlerContext.close();
        }
    }

    /**
     * 判断能否升级websocket
     * @param httpRequest http请求
     * @return 返回判断结果
     */
    private boolean canUpgrade(HttpRequest httpRequest) {
        HttpHeaders httpHeaders = httpRequest.headers();

        if (httpHeaders.contains(HEADER_UPGRADE_KEY) && httpHeaders.get(HEADER_UPGRADE_KEY).equals(HEADER_UPGRADE_VALUE)
                && httpHeaders.contains(HEADER_WEBSOCKET_KEY) && httpHeaders.get(HEADER_WEBSOCKET_KEY).equals(HEADER_WEBSOCKET_VERSION)) {
            return true;
        }

        return false;
    }
}
