package com.baozi.code.impl;

import com.baozi.code.BusinessMessageEncoder;

/**
 * @Description: 原始消息封装器,只把msg原封不动的传出去
 * @Author: lirl
 * @Create: 2019-01-07 14:53
 */
public class OriginBusinessMessageEncoder implements BusinessMessageEncoder {

    @Override
    public String encode(String msg, String ack, String type) {
        return msg;
    }
}
