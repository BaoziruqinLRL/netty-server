package com.baozi.data;

import lombok.Data;

/**
 * @Description: 传输数据类
 * @Author: baozi
 * @Create: 2018-09-25 12:22
 */
@Data
public class TransferData {

    /**
     * 传输类型
     */
    private String type;

    /**
     * 业务数据
     */
    private String content;

    /**
     * 回馈消息
     */
    private String ack;
}
