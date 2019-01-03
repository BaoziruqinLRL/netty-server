package com.baozi.cache;

import com.baozi.business.BusinessExecutor;
import com.baozi.data.ChannelParam;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description: 用户channel缓存存储类
 * @Author: baozi
 * @Create: 2018-09-25 11:47
 */
public class ChannelCache {

    private static final int DEFAULT_INIT_CAPACITY = 100;
    private static final int DEFAULT_INIT_REST_CAPACITY = 10;

    /**
     * 存储所有用户的channel，key为用户id
     */
    private static ConcurrentMap<String, ChannelParam> allUser = new ConcurrentHashMap<>(DEFAULT_INIT_CAPACITY);

    /**
     * 存储所有用户的channel，key为channelId
     */
    private static ConcurrentMap<ChannelId,ChannelParam> allUserByChannelId = new ConcurrentHashMap<>(DEFAULT_INIT_CAPACITY);

    /**
     * 存储一个餐厅下的用户channelGroup，key为餐厅id，value为一个餐厅目前在线的所有channel的group
     */
    private static ConcurrentMap<String, ChannelGroup> restChannelGroup = new ConcurrentHashMap<>(DEFAULT_INIT_REST_CAPACITY);

    /**
     * 业务处理器map，key为业务处理器名称
     */
    private static Map<String, BusinessExecutor> executorMap = new HashMap<>();

    /**
     * 存储单个channel，存储成功则存入组中
     * @param channel channel
     */
    public static void storeSingleUser(ChannelParam channel){
        if (channel == null || channel.getClientId() == null){
            return;
        }
        var value = allUser.putIfAbsent(channel.getClientId(),channel);
        boolean userStore = false;
        if (value != null){
            if (channel.getLastTimestamp() > value.getLastTimestamp()){
                allUser.put(channel.getClientId(),channel);
                allUserByChannelId.put(channel.getChannel().id(),channel);
                userStore = true;
            }
        }else{
            allUserByChannelId.put(channel.getChannel().id(),channel);
            userStore = true;
        }
        // 存储至组map
        if (userStore){
            if (restChannelGroup.containsKey(channel.getGroupId())){
                var group = restChannelGroup.get(channel.getGroupId());
                group.add(channel.getChannel());
            }else{
                ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
                group.add(channel.getChannel());
                restChannelGroup.put(channel.getGroupId(),group);
            }
        }
    }

    /**
     * 根据客户端id获取channel
     * @param clientId ???id
     * @return channel对象
     */
    public static ChannelParam client(String clientId){
        if (clientId != null){
            return allUser.get(clientId);
        }
        return null;
    }

    /**
     * 根据组id获取channel
     * @param groupId ?id
     * @return channel对象
     */
    public static ChannelGroup group(String groupId){
        if (groupId != null){
            return restChannelGroup.get(groupId);
        }
        return null;
    }

    /**
     * 根据channelId获取channel
     * @param channelId channelId
     * @return channel对象
     */
    public static ChannelParam clientByChannelId(ChannelId channelId){
        if (channelId != null){
            return allUserByChannelId.get(channelId);
        }
        return null;
    }

    /**
     * 根据客户端id删除channel
     * @param clientId ???id
     * @return 移除的channel
     */
    public static ChannelParam removeChannel(String clientId){
        if (clientId != null && allUser.containsKey(clientId)){
            var value = allUser.remove(clientId);
            allUserByChannelId.remove(value.getChannel().id());
            restChannelGroup.get(value.getGroupId()).remove(value.getChannel());
            return value;
        }
        return null;
    }

    /**
     * 根据channelid删除channel
     * @param channelId channeld
     * @return 移除的channel
     */
    public static ChannelParam removeChannelByChannelId(ChannelId channelId){
        if (channelId != null && allUserByChannelId.containsKey(channelId)){
            var value = allUserByChannelId.remove(channelId);
            allUser.remove(value.getClientId());
            return value;
        }
        return null;
    }

    /**
     * 返回channel数量
     * @return size
     */
    public static int channelSize(){
        return allUser.size();
    }

    /**
     * 根据组id获取sieze
     * @param groupId 若id为空返回组数量，否则返回组中的元素数量
     * @return size
     */
    public static int groupSize(String groupId){
        if (StringUtils.isEmpty(groupId)){
            return restChannelGroup.size();
        }else{
            if (restChannelGroup.containsKey(groupId)){
                return restChannelGroup.get(groupId).size();
            }else {
                return 0;
            }
        }
    }

    public static void setExecutorMap(Map<String, BusinessExecutor> executorMap){
        ChannelCache.executorMap.putAll(executorMap);
    }

    public static BusinessExecutor getExecutor(String type){
        return executorMap.getOrDefault(type,null);
    }
}
