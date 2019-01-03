package com.baozi.business;

import com.baozi.cache.ChannelCache;
import com.baozi.data.ChannelParam;
import com.baozi.data.TransferData;
import com.baozi.util.ExecutorUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 客户端业务分配器（服务端分配客户端的任务）
 * @Author: baozi
 * @Create: 2018-09-25 12:38
 */
@Slf4j
public class BusinessWorker {

    /**
     * 默认线程存活时间
     */
    private static final long ALIVE_TIME = 60L;

    /**
     * 默认线程池大小
     */
    private static final int POOL_SIZE = 4;

    /**
     * 线程池名称
     */
    private static final String DEFAULT_POOL_NAME = "business-worker";

    /**
     * 任务执行线程池
     */
    private static ExecutorService executorPool = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, ALIVE_TIME,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(Integer.MAX_VALUE), ExecutorUtil.buildNameThreadFactory(DEFAULT_POOL_NAME));

    public static void distributeTask(TransferData data, Channel channel){
        executorPool.submit(new ExecutorTask(data,channel));
    }

    private static class ExecutorTask implements Runnable{

        private TransferData data;

        private Channel channel;

        ExecutorTask(TransferData data, Channel channel) {
            this.data = data;
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                var executor = ChannelCache.getExecutor(data.getType());
                if (executor != null) {
                    // 执行任务
                    var cacheId = executor.exec(data);
                    if (cacheId != null) {
                        // 存储channel
                        ChannelCache.storeSingleUser(new ChannelParam(cacheId.getClientId(), cacheId.getGroupId(), channel, cacheId.getTimestamp()));
                        if (cacheId.getAckMessage() != null) {
                            BusinessNotify.noticeSingleClient(cacheId.getClientId(), cacheId.getAckMessage());
                        }
                    }
                } else {
                    // 日志打印
                    log.error("No exist Executor by type: " + data.getType());
                }
            }catch (Exception ex){
                log.error("business worker exception {}",ex);
            }
        }
    }
}
