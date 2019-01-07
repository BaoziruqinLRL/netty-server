package com.baozi.code;

import com.baozi.data.TransferData;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @Description:
 * @Author: baozi
 * @Create: 2019-01-07 12:04
 */
public interface BusinessMessageDecoder {

    /**
     * 解析器,将websocket消息解析成transferData
     * @param msg websocket消息
     * @return 解析结果
     */
    TransferData decode(TextWebSocketFrame msg);
}
