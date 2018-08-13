package com.netty.message;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.netty.util.JsonUtil;

import java.util.Date;

/**
 * websocket message - TEXT
 *
 * @author 千阳
 * @date 2018-07-29
 */
public class MogoWsTextMessage extends AbstractStatusAwareWsMessage{

    public MogoWsTextMessage(){
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

    public static void main(String[] args){
        MogoWsTextMessage message = new MogoWsTextMessage();
        WsMessageEndpoint from = new WsMessageEndpoint();
        from.setCtctId("1");
        from.setCtctIdSource("1");
        WsMessageEndpoint to = new WsMessageEndpoint();
        to.setCtctId("7");
        to.setCtctIdSource("7");
        message.setFrom(from);
        message.setTo(to);
        message.setMessageType(WsMessageEnum.WsMessageType.TEXT);
        message.setMsgBusinessType(WsMessageEnum.WsMsgBusinessType.FIND);

        System.out.println(message);
    }
}
