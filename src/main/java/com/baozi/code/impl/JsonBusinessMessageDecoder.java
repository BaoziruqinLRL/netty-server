package com.baozi.code.impl;

import com.alibaba.fastjson.JSON;
import com.baozi.code.AbstractBusinessMessageDecoder;
import com.baozi.data.TransferData;

/**
 * @Description: 一个默认的解析器,该解析器会把数据当做json格式进行解析
 * @Author: baozi
 * @Create: 2019-01-07 12:07
 */
public class JsonBusinessMessageDecoder extends AbstractBusinessMessageDecoder {

    @Override
    protected TransferData businessDecode(String text) {
        return JSON.parseObject(text,TransferData.class);
    }
}
