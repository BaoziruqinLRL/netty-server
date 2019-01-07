package com.baozi.code.impl;

import com.alibaba.fastjson.JSON;
import com.baozi.code.BusinessMessageEncoder;
import com.baozi.data.SendData;

/**
 * @Description: json的重发数据结构构建器
 * @Author: baozi
 * @Create: 2019-01-07 13:43
 */
public class JsonBusinessMessageEncoder implements BusinessMessageEncoder {

    @Override
    public String encode(String msg,String ack,String type) {
        // 重发消息则构建重发数据结构
        var sendData = new SendData();
        sendData.setAck(ack);
        sendData.setContent(msg);
        sendData.setType(type);
        return JSON.toJSONString(sendData);
    }

}
