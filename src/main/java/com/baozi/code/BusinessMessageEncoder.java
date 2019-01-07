package com.baozi.code;

/**
 * @Description: 重发数据封装器
 * @Author: baozi
 * @Create: 2019-01-07 13:30
 */
public interface BusinessMessageEncoder {

    /**
     * 封装重发数据
     * @param msg 消息
     * @param ack 重试key
     * @param type 消息类型
     * @return 封装后的消息
     */
    String encode(String msg, String ack, String type);
}
