package com.baozi.business;

import com.baozi.cache.ChannelCache;
import com.baozi.constructor.ServerConstructor;
import com.baozi.data.ResendData;
import com.baozi.retry.RetryCache;
import com.baozi.util.KeyUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @Description: 服务端业务通知器（向客户端发起通知）
 * @Author: baozi
 * @Create: 2018-09-25 13:06
 */
public class BusinessNotify {

    /**
     * 通知单个客户端，且无需重发
     * @param clientId 客户端id
     * @param msg 通知消息
     * @return 通知结果
     */
    public static boolean noticeSingleClient(String clientId, String msg) {
        return noticeSingleClient(clientId, msg, false,false, null);
    }

    /**
     * 通知单个客户端，需要重发的不可靠消息
     * @param clientId 客户端id
     * @param msg 通知消息
     * @return 通知结果
     */
    public static boolean noticeSingleClient(String clientId, String msg, Boolean resend) {
        return noticeSingleClient(clientId, msg, resend, false, null);
    }

    /**
     * 通知单个客户端，且无需重发, 带类型
     * @param clientId 客户端id
     * @param msg 通知消息
     * @return 通知结果
     */
    public static boolean noticeSingleClient(String clientId, String msg,String type) {
        return noticeSingleClient(clientId, msg, false,false, type);
    }

    /**
     * 通知单个客户端，需要重发的不可靠消息, 带类型
     * @param clientId 客户端id
     * @param msg 通知消息
     * @return 通知结果
     */
    public static boolean noticeSingleClient(String clientId, String msg, Boolean resend,String type) {
        return noticeSingleClient(clientId, msg, resend, false, type);
    }

    /**
     * 通知单个客户端, 可选择重发, 可选择可靠与否
     * @param clientId 客户端id
     * @param msg 通知消息
     * @param reliable 是否可靠消息,可靠消息将依据时间片轮转最长重发时间无限重发,请谨慎使用
     * @return 通知结果
     */
    public static boolean noticeSingleClient(String clientId, String msg, Boolean resend,Boolean reliable,String type){
        var channelParam = ChannelCache.client(clientId);
        if (channelParam != null) {
            if (resend) {
                // 重发消息则构建重发数据结构
                var ack = KeyUtil.buildResendKey();
                var sendDataMsg = ServerConstructor.getBusinessMessageEncoder().encode(msg,ack,type);
                channelParam.getChannel().writeAndFlush(new TextWebSocketFrame(sendDataMsg));
                // 数据加入重试缓存
                var resendData = new ResendData();
                resendData.setClientId(clientId);
                // 初始设定为1,因为一旦加入时间片轮转后立即开始轮转,count为1表明第一次轮转开始
                resendData.setCount(1);
                resendData.setSendData(sendDataMsg);
                resendData.setReliable(reliable);
                RetryCache.putData(ack, resendData);
                // 加入时间片轮转
                if (RetryCache.isWheel()){
                    RetryCache.timeWheelProcessor().addToWheel(ack);
                }
            }else{
                // 非重发消息直接发送数据
                channelParam.getChannel().writeAndFlush(new TextWebSocketFrame(ServerConstructor.getBusinessMessageEncoder().encode(msg,null,type)));
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * 通知一组客户端
     * @param groupId 组id
     * @param msg 通知消息
     * @return 通知结果
     */
    public static boolean noticeGroup(String groupId,String msg){
        return noticeGroup(groupId,msg,null);
    }

    /**
     * 通知一组客户端,带类型
     * @param groupId 组id
     * @param msg 通知消息
     * @param type 消息类型
     * @return 通知结果
     */
    public static boolean noticeGroup(String groupId,String msg,String type){
        var group = ChannelCache.group(groupId);
        if (group != null){
            group.writeAndFlush(new TextWebSocketFrame(ServerConstructor.getBusinessMessageEncoder().encode(msg,null,type)));
            return true;
        }else{
            return false;
        }
    }
}
