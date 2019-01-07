# Netty-server Tool 使用说明
1.本工具目前仅支持websocket长连接通信，当然客户端需要也支持长连接  
2.netty版本4.1.29.Final  

# 服务构造方式
example：
``` JAVA
public class ServerBootConfig{
    
    @PostConstruct
    private void init(){
        Map<String, BusinessExecutor> executorMap = new HashMap<>(2);
        executorMap.put("verify", new DeviceVerifyExecutor());
        executorMap.put(ExecutorConstant.LOST_CONNECT_KEY,new LostConnectExecutor());
        ServerConstructor.setExecutorMap(executorMap);
        ServerConstructor.setPort(port);
        // 心跳消息不设置时默认也为这两个值,这里只是做一个示范
        ServerConstructor.setHeartbeatType("server-heartbeat");
        ServerConstructor.setHeartbeatReply("client-heartbeat");
        // 毫秒级时间
        ServerConstructor.setResendTimeWheel(new long[]{100,500,1000,5000});
        ServerConstructor.setServerHeartbeat(false);
        // 可自定义消息解析器和重发数据构造器
        ServerConstructor.setBusinessMessageDecode(new MapBusinessMessageDecoder());
        ServerConstructor.setBusinessMessageEncode(new JsonBusinessMessageEncoder());
        // 启动
        serverConstructor.start();
    }
}
```
## 构造属性说明
`port`: `Integer 服务端口，默认8080`  
`heartBeatType`: `String 服务端发送的心跳消息，默认为"server-heartbeat"`  
`heartBeatReply`: `String 客户端回复的心跳消息，默认为"client-heartbeat"`  
`serverHeartBeat`: `boolean 是否开启服务端主动心跳`  
`executorMap`: `Map<String,BusinessExecutor> 执行器map，key为消息类型，value为对应类型的执行器（执行器由依赖方实现BusinessExecutor接口）.其中ExecutorConstant.LOST_CONNECT_KEY若不设置则会默认设置一个空实现的失联通知器,且不影响已有业务.`  
`resendTimeWheel`: `重试时间片间隔,可定义任意长度的long数组.重试时若设置了可靠消息,则会以时间片间隔的最后一个重试时间无限重试`
`businessMessageDecoder`: `消息解析器,可实现BusinessMessageDecoder接口自定义实现,不传则默认使用内置的Json解析器`
`businessMessageEncode`: `发送数据构造器,可实现BusinessMessageEncoder接口自定义实现,不传则默认使用内置的Json构造器`

## 消息通知说明
### 发送单向通知
```
BusinessNotify.noticeSingleClient(clientId, msg, true, reliable);
```
其中参数说明如下:    
`clientId`: `netty缓存存储的channel对应的key,需要业务方自己确定clientId并传入`    
`msg`: `消息实体,string字符串`    
`resend`: `是否重发`    
`reliable`: `消息是否可靠,不可靠则以时间片间隔重发,发完即止; 可靠消息则取时间片间隔最后一个重试时间无限重发`    
### 发送组通知
```
BusinessNotify.noticeGroup(groupId,msg);
```
目前组通知不支持重试
## 默认发送的数据结构说明
默认发送的数据结构为一个固定结构,如下:
```
@Data
public class SendData {

    /**
     * 反馈消息
     */
    private String ack;

    /**
     * 实际消息
     */
    private String content;
    
    /**
     * 传输类型
     */
    private String type;
}
```
其中ack为客户端需要回复的确认收货的消息,客户端只需在接收到消息后直接回复ack对应的数据即可确认;content为实际消息.
服务器可以自己实现BusinessMessageEncoder接口,以构建自定义的数据结构.
## BusinessExecutor说明
业务执行接口，接口仅有一个exec方法，依赖方可以根据不同的业务做不同的实现，并赋予不同的type，在服务初始化时将所有Executor通过ServerConstructor进行初始化. 所有Executor接收的数据类型为固定类型,如下:
```
@Data
public class TransferData {

    /**
     * 传输类型
     */
    private String type;

    /**
     * 业务数据
     */
    private String content;

    /**
     * 回馈消息
     */
    private String ack;
}
```
```
特别注意: exec需要返回一个CacheIdParam类型，其中包含clientId,groupId,timestamp和ackMessage.    
clientId: 长连接channel的唯一id,必须返回,做为缓存存储,否则无法发送消息,需要业务执行器自己判断该clientId并返回,且改id是可变的      
groupId: 长连接channel的组id，用于对一个组别批量通知  
timestamp: 消息时间戳  
ackMessage: 回复消息，表示数据成功处理后要回复给客户端的消息,可以看做是客户端重发需要回复的ack   
```
## BusinessMessageEncoder说明
重发数据构造接口,构造发送的数据.
内置了JsonBusinessMessageEncoder和OriginBusinessMessageEncoder.

## BusinessMessageDecoder说明
消息解析器,需要将消息解析成TransferData结构.
内置了JsonBusinessMessageDecoder和MapBusinessMessageDecoder.

## DEMO 请移步[netty-server-demo](https://github.com/BaoziruqinLRL/netty-server-demo) 和 [netty-client-demo](https://github.com/BaoziruqinLRL/netty-client-demo). 启动即可测试.
