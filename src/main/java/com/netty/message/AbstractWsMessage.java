package com.netty.message;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.netty.util.JsonUtil;

import java.util.Date;

/**
 * websocket message
 *
 * @author 千阳
 * @date 2018-07-29
 */
public abstract class AbstractWsMessage {
    /**
     * 消息唯一标识
     */
    protected String id;
    /**
     * 发消息方描述
     */
    protected WsMessageEndpoint from;
    /**
     * 收消息方描述
     */
    protected WsMessageEndpoint to;
    /**
     * 消息类型
     */
    protected WsMessageEnum.WsMessageType messageType;
    /**
     * 消息业务类型
     */
    protected WsMessageEnum.WsMsgBusinessType msgBusinessType;

    /**
     * 消息发送时间
     */
    protected Date sendTime;

    public WsMessageEndpoint getFrom() {
        return from;
    }

    public void setFrom(WsMessageEndpoint from) {
        this.from = from;
    }

    public WsMessageEndpoint getTo() {
        return to;
    }

    public void setTo(WsMessageEndpoint to) {
        this.to = to;
    }

    public WsMessageEnum.WsMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(WsMessageEnum.WsMessageType messageType) {
        this.messageType = messageType;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WsMessageEnum.WsMsgBusinessType getMsgBusinessType() {
        return msgBusinessType;
    }

    public void setMsgBusinessType(WsMessageEnum.WsMsgBusinessType msgBusinessType) {
        this.msgBusinessType = msgBusinessType;
    }

}
