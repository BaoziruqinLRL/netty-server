package com.baozi.data;

import lombok.Data;

/**
 * @Description: netty重发数据
 * @Author: baozi
 * @Create: 2018-12-05 15:07
 */
@Data
public class ResendData {

    /**
     * 重试次数
     */
    private Integer count;

    /**
     * channel用户id
     */
    private String clientId;

    /**
     * 需要发送的消息
     */
    private String sendData;

    /**
     * 是否可靠消息
     */
    private boolean reliable;

    public void addCount(){
        if (count == 0){
            count = 1;
        }else{
            count++;
        }
    }
}
