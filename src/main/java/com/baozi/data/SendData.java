package com.baozi.data;

import lombok.Data;

/**
 * @Description: netty发送数据
 * @Author: baozi
 * @Create: 2018-12-05 15:06
 */
@Data
public class SendData {

    /**
     * 反馈消息
     */
    private String ack;

    /**
     * 实际消息
     */
    private String content;
}
