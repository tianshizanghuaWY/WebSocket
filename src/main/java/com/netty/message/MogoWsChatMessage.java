package com.netty.message;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.netty.util.JsonUtil;

import java.util.Date;

/**
 * websocket - 文本聊天消息
 *
 * @author 千阳
 * @date 2018-07-29
 */
public class MogoWsChatMessage extends AbstractStatusAwareWsMessage{

    public MogoWsChatMessage(){
        this.messageType = WsMessageEnum.WsMessageType.TEXT;
    }

    @Override
    public WsMessageEnum.WsMessageType getMessageType() {
        if(this.messageType == null){
            this.messageType = WsMessageEnum.WsMessageType.TEXT;
        }

        return this.messageType;
    }

    /**
     * 消息内容
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat});
    }

    /**
     * 判断 ws 消息是否属于文本聊天消息
     * @param messageType
     * @param businessType
     * @return
     */
    public static boolean matched(String messageType, String businessType){
        if(WsMessageEnum.WsMessageType.TEXT.name().equals(messageType)
                &&(WsMessageEnum.WsMsgBusinessType.FIND.name().equals(businessType)
                    ||WsMessageEnum.WsMsgBusinessType.SERVICE.name().equals(businessType))){
            return true;
        }
        return false;
    }

    public static void main(String[] args){
        MogoWsChatMessage message = new MogoWsChatMessage();
        WsMessageEndpoint from = new WsMessageEndpoint();
        from.setCtctId("1");
        from.setCtctIdSource("1");
        from.setIp("192.168.1.1");
        from.setName("192.168.1.1");
        WsMessageEndpoint to = new WsMessageEndpoint();
        to.setCtctId("7");
        to.setCtctIdSource("7");
        to.setIp("192.168.1.1");
        to.setName("192.168.1.1");
        message.setFrom(from);
        message.setTo(to);
        message.setMessageType(WsMessageEnum.WsMessageType.TEXT);
        message.setMsgBusinessType(WsMessageEnum.WsMsgBusinessType.FIND);
        message.setMessage("找房信息");
        message.setId("121212");
        message.setSendTime(new Date());
        message.setPostTime(new Date());
        message.setReadTime(new Date());
        message.setMessageStatus(WsMessageEnum.WsMessageStatus.READ);

        System.out.println(message);
    }
}
