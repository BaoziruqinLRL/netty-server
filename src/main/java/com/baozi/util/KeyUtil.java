package com.baozi.util;

import java.util.UUID;

/**
 * @Description: key生成工具
 * @Author: baozi
 * @Create: 2018-12-05 15:40
 */
public class KeyUtil {

    public static final String ACK_KEY = "NETTY_MESSAGE_ACK";

    public static String buildResendKey(){
        return ACK_KEY + ":" + UUID.randomUUID().toString();
    }
}
