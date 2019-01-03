package com.baozi.retry;

import com.baozi.cache.ChannelCache;
import com.baozi.constructor.ServerConstructor;
import com.baozi.util.timewheel.TimeoutNotification;
import com.baozi.util.timewheel.TimerWheel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @Description: 通知消息时间片轮转处理器
 * @Author: baozi
 * @Create: 2018-12-05 14:24
 */
@Slf4j
public class NoticeTimeWheelProcessor {

    private TimerWheel<String> timerWheel = new TimerWheel<>(new RetryWheelImpl());

    /**
     * 默认重试时间片间隔
     */
    private long[] defaultTimeWheel = new long[]{1000L,5*1000L,10*1000L,60*1000L,30*60*1000L,60*60*1000L};
    private long[] timeWheel = Optional.ofNullable(ServerConstructor.getResendTimeWheel()).orElse(defaultTimeWheel);

    /**
     * 添加至轮转
     * @param key 轮转key
     */
    public void addToWheel(String key){
        if (key == null){
            return;
        }
        timerWheel.add(key,timeWheel[0]);
    }

    /**
     * 从轮转中移除
     * @param key 移除数据
     */
    public void removeFromWheel(String key){
        if (key == null){
            return;
        }
        timerWheel.remove(key);
    }

    private class RetryWheelImpl implements TimeoutNotification<String> {

        @Override
        public long notice(String o) {
            long res = 0L;
            try {
                var data = RetryCache.getData(o);
                // 轮转到此处,发送一次数据
                var clientId = data.getClientId();
                var channelParam = ChannelCache.client(clientId);
                if (channelParam != null) {
                    channelParam.getChannel().writeAndFlush(new TextWebSocketFrame(data.getSendData()));
                }
                if (data.getCount() < timeWheel.length) {
                    // 获取下一次轮转的时间间隔
                    res = timeWheel[data.getCount()];
                    // 数据加入重试缓存
                    data.addCount();
                    RetryCache.putData(o, data);
                }else if (data.isReliable()){
                    // 若是可靠消息,则始终以时间片间隔的最后的重发时间来重发
                    res = timeWheel[timeWheel.length - 1];
                }
            }catch (Exception ex){
                log.error("netty retry exception {}.",ex);
            }
            return res;
        }
    }
}
