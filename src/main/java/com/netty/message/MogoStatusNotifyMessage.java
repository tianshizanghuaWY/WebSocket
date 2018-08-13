package com.netty.message;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.netty.util.JsonUtil;

/**
 * websocket message - TEXT
 *
 * @author 千阳
 * @date 2018-07-29
 */
public class MogoStatusNotifyMessage extends AbstractWsMessage{

    public MogoStatusNotifyMessage(){
        this.messageType = WsMessageEnum.WsMessageType.TEXT;
        this.msgBusinessType = WsMessageEnum.WsMsgBusinessType.MESSAGE_STATE;
    }

    /**
     * 消息id
     */
    private String messageId;
    private WsMessageEnum.WsMessageStatus status;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public WsMessageEnum.WsMessageStatus getStatus() {
        return status;
    }

    public void setStatus(WsMessageEnum.WsMessageStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat});
    }

    public static MogoStatusNotifyMessage buildNotifyMessage(AbstractStatusAwareWsMessage message, WsMessageEnum.WsMessageStatus status){
        if(status == null){
            status = WsMessageEnum.WsMessageStatus.POSTED;
        }
        if(message == null || message.getId() == null){
            return null;
        }

        MogoStatusNotifyMessage result = new MogoStatusNotifyMessage();
        result.setMessageId(message.getId());
        result.setTo(message.getFrom());
        result.setStatus(status);

        return result;
    }
}
