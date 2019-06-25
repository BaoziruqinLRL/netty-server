package com.baozi.constructor;

import com.baozi.bootstrap.NettyServerBootstrap;
import com.baozi.business.BusinessExecutor;
import com.baozi.business.impl.DefaultLostConnectExecutor;
import com.baozi.cache.ChannelCache;
import com.baozi.code.BusinessMessageDecoder;
import com.baozi.code.BusinessMessageEncoder;
import com.baozi.code.impl.JsonBusinessMessageDecoder;
import com.baozi.code.impl.JsonBusinessMessageEncoder;
import com.baozi.constant.ExecutorConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Description: 服务构造器，这里将会用传入的参数构造并启动netty，依赖方只需构造这个方法即可
 * @Author: lirl
 * @Create: 2018-09-25 15:15
 */
public class ServerConstructor {

    /**
     * 服务端口
     */
    private static int port;

    /**
     * 默认线程存活时间，分钟;不设置可填-1，默认设置60分钟
     */
    private static long aliveTime;

    /**
     * 默认线程池大小;不设置可填-1，默认设置2
     */
    private static int poolSize;

    /**
     * 客户端心跳类型
     */
    private static String heartbeatType;

    /**
     * 服务端心跳回复类型
     */
    private static String heartbeatReply;

    /**
     * 是否开启服务端主动心跳
     */
    private static boolean serverHeartbeat;

    /**
     * 是否开启ssl
     */
    private static boolean ssl;

    /**
     * ssl证书类型
     */
    private static String sslKeyType;

    /**
     * ssl证书路径
     */
    private static String sslKeyPath;

    /**
     * ssl证书密码
     */
    private static String sslKeyPwd;

    /**
     * 重发间隔,可定义任意长度的重发时间
     */
    private static long[] resendTimeWheel;

    /**
     * 消息解析器
     */
    private static BusinessMessageDecoder businessMessageDecode;

    /**
     * 重发数据构造器
     */
    private static BusinessMessageEncoder businessMessageEncoder;

    public static void start(){
        // 设定一个默认的失联回调接口
        if (ChannelCache.getExecutor(ExecutorConstant.LOST_CONNECT_KEY) == null){
            var lostMap = new HashMap<String,BusinessExecutor>(1);
            lostMap.put(ExecutorConstant.LOST_CONNECT_KEY,new DefaultLostConnectExecutor());
            ChannelCache.setExecutorMap(lostMap);
        }
        // 设定默认的消息解析器
        businessMessageDecode = Optional.ofNullable(businessMessageDecode).orElse(new JsonBusinessMessageDecoder());
        // 设定默认的重发数据构造器
        businessMessageEncoder = Optional.ofNullable(businessMessageEncoder).orElse(new JsonBusinessMessageEncoder());
        NettyServerBootstrap serverBootstrap = new NettyServerBootstrap(port);
        serverBootstrap.start(heartbeatType,heartbeatReply,serverHeartbeat);
    }

    public static void setExecutorMap(Map<String, BusinessExecutor> executorMap) {
        ChannelCache.setExecutorMap(executorMap);
    }

    public static void setPort(int port){
        ServerConstructor.port = port;
    }

    public static void setAliveTime(long aliveTime){
        ServerConstructor.aliveTime = aliveTime;
    }

    public static void setPoolSize(int poolSize){
        ServerConstructor.poolSize = poolSize;
    }

    public static void setHeartbeatType(String heartbeatType){
        ServerConstructor.heartbeatType = heartbeatType;
    }

    public static String getHeartbeatType(){
        return heartbeatType;
    }

    public static void setHeartbeatReply(String heartbeatReply){
        ServerConstructor.heartbeatReply = heartbeatReply;
    }

    public static String getHeartbeatReply(){
        return heartbeatReply;
    }

    public static void setSsl(boolean ssl){
        ServerConstructor.ssl = ssl;
    }

    public static boolean getSsl(){
        return ServerConstructor.ssl;
    }

    public static void setSslKeyType(String sslKeyType){
        ServerConstructor.sslKeyType = sslKeyType;
    }

    public static String getSslKeyType(){
        return ServerConstructor.sslKeyType;
    }

    public static void setSslKeyPath(String sslKeyPath){
        ServerConstructor.sslKeyPath = sslKeyPath;
    }

    public static String getSslKeyPath(){
        return ServerConstructor.sslKeyPath;
    }

    public static void setSslKeyPwd(String sslKeyPwd){
        ServerConstructor.sslKeyPwd = sslKeyPwd;
    }

    public static String getSslKeyPwd(){
        return ServerConstructor.sslKeyPwd;
    }

    public static void setServerHeartbeat(boolean serverHeartbeat){
        ServerConstructor.serverHeartbeat = serverHeartbeat;
    }

    public static void setResendTimeWheel(long[] resendTimeWheel){
        ServerConstructor.resendTimeWheel = resendTimeWheel;
    }

    public static long[] getResendTimeWheel(){
        return resendTimeWheel;
    }

    public static BusinessMessageDecoder getBusinessMessageDecode() {
        return businessMessageDecode;
    }

    public static void setBusinessMessageDecode(BusinessMessageDecoder businessMessageDecode) {
        ServerConstructor.businessMessageDecode = businessMessageDecode;
    }

    public static BusinessMessageEncoder getBusinessMessageEncoder() {
        return businessMessageEncoder;
    }

    public static void setBusinessMessageEncoder(BusinessMessageEncoder businessMessageEncoder) {
        ServerConstructor.businessMessageEncoder = businessMessageEncoder;
    }
}
