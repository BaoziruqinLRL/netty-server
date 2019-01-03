package com.baozi.business.impl;

import com.baozi.business.BusinessExecutor;
import com.baozi.data.CacheIdParam;
import com.baozi.data.TransferData;
import org.springframework.stereotype.Component;

/**
 * @Description: 默认的失联通知器
 * @Author: baozi
 * @Create: 2018-12-18 18:33
 */
@Component
public class DefaultLostConnectExecutor implements BusinessExecutor {

    @Override
    public CacheIdParam exec(TransferData data) {
        return null;
    }
}
