package com.baozi.util;

import com.alibaba.fastjson.JSON;
import com.baozi.constructor.ServerConstructor;
import com.baozi.data.TransferData;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: websocket消息解析
 * @Author: baozi
 * @Create: 2018-09-25 10:53
 */
public class WebSocketFrameUtil {

    /**
     * webSocket 消息解析
     * @param frame 消息
     * @return 解析结果
     */
    public static TransferData decode(TextWebSocketFrame frame) {
        String text = frame.text();
        if (StringUtils.isEmpty(text)){
            return new TransferData();
        }
        TransferData transferData = new TransferData();
        // 如果不是json字符串，则认为该消息为心跳消息，将整个消息设置到type中
        if (ServerConstructor.getHeartbeatType().equals(text) ||
                ServerConstructor.getHeartbeatReply().equals(text)){
            transferData.setType(text);
        }else if (text.startsWith(KeyUtil.ACK_KEY)){
            // ack回复
            transferData.setAck(text);
        }else{
            transferData = JSON.parseObject(text,TransferData.class);
        }
        return transferData;
    }
}
