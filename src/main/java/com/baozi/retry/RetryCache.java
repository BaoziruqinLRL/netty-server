package com.baozi.retry;


import com.baozi.data.ResendData;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description: 重试数据缓存类
 * @Author: baozi
 * @Create: 2018-12-05 15:27
 */
public class RetryCache {

    /**
     * 重试缓存,记录重试ack-key和重试数据的映射
     */
    private static ConcurrentMap<String, ResendData> resendMap = new ConcurrentHashMap<>(16);

    /**
     * 时间片处理器
     */
    private static NoticeTimeWheelProcessor timeWheelProcessor = new NoticeTimeWheelProcessor();

    public static void putData(String key, ResendData data){
        resendMap.put(key,data);
    }

    public static ResendData getData(String key){
        return resendMap.get(key);
    }

    public static NoticeTimeWheelProcessor timeWheelProcessor(){
        return timeWheelProcessor;
    }

    public static boolean isWheel(){
        return timeWheelProcessor != null;
    }

}
