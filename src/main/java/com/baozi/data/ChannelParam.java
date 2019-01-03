package com.baozi.data;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @Description: channel存储类
 * @Author: baozi
 * @Create: 2018-09-25 12:01
 */
@Data
public class ChannelParam {

    public ChannelParam(String clientId, String groupId, Channel channel, Long lastTimestamp) {
        this.clientId = clientId;
        this.groupId = groupId;
        this.channel = channel;
        this.lastTimestamp = lastTimestamp;
    }

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 组id
     */
    private String groupId;

    /**
     * 客户端channel
     */
    private Channel channel;

    /**
     * 时间戳。对于缓存中的对象则为上次更新时间戳，对于传入对象则为当前操作时间戳
     */
    private Long lastTimestamp;

}
