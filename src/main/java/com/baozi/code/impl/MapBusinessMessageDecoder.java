package com.baozi.code.impl;

import com.baozi.code.AbstractBusinessMessageDecoder;
import com.baozi.data.TransferData;

/**
 * @Description: 将键值对类型的消息解析成transferData
 * @Author: baozi
 * @Create: 2019-01-07 12:39
 */
public class MapBusinessMessageDecoder extends AbstractBusinessMessageDecoder {

    @Override
    protected TransferData businessDecode(String text) {
        var transferData = new TransferData();
        // 键值对消息类型例如 message:{"type":"message","content":{"a":123,"b":234}}
        if (text.contains(":")) {
            String[] strs = text.split(":", 2);
            transferData.setType(strs[0]);
            transferData.setContent(strs[1]);
        } else {
            transferData.setType(text);
        }
        return transferData;
    }
}
