package com.baozi.data;

/**
 * @Description: 缓存id参数
 * @Author: baozi
 * @Create: 2018-09-29 18:39
 */
public class CacheIdParam {

    public CacheIdParam(String clientId, String groupId, Long timestamp) {
        this.clientId = clientId;
        this.groupId = groupId;
        this.timestamp = timestamp;
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
     * 时间戳
     */
    private Long timestamp;

    /**
     * 返回给客户端的消息
     */
    private String ackMessage;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAckMessage() {
        return ackMessage;
    }

    public void setAckMessage(String ackMessage) {
        this.ackMessage = ackMessage;
    }
}
