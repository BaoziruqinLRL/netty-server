package com.baozi.code;

import com.baozi.constructor.ServerConstructor;
import com.baozi.data.TransferData;
import com.baozi.util.KeyUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 抽象的消息解析实现,封装了心跳消息解析和ACK回复解析,
 * 具体的子实现类只需要实现businessDecode
 * @Author: baozi
 * @Create: 2019-01-07 12:30
 */
public abstract class AbstractBusinessMessageDecoder implements BusinessMessageDecoder {

    @Override
    public TransferData decode(TextWebSocketFrame frame) {
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
            transferData = businessDecode(text);
        }
        return transferData;
    }

    /**
     * 业务实现类,子实现只需要关心将业务数据类型转换成transferData即可,无需关心心跳和ACK类型的消息判断
     * @param text 消息字符串
     * @return 解析结果
     */
    protected abstract TransferData businessDecode(String text);
}
