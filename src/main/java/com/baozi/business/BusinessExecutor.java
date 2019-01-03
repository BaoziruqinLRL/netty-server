package com.baozi.business;

import com.baozi.data.CacheIdParam;
import com.baozi.data.TransferData;

/**
 * @Description: 业务执行器，由子类实现
 * @Author: baozi
 * @Create: 2018-09-25 12:26
 */
public interface BusinessExecutor {

    /**
     * 业务实现方法
     * @param data 传输数据
     * @return 执行结果.需要返回客户端id，组id和时间戳，具体的操作由业务实现
     */
    CacheIdParam exec(TransferData data);
}
