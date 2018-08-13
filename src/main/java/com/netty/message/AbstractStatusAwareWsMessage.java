package com.netty.message;

import java.util.Date;

/**
 * 可回复的websocket message
 * 可回复包括: 系统自动回复消息状态, 客户端修改消息状态时触发系统回复
 *
 * @author 千阳
 * @date 2018-07-29
 */
public abstract class AbstractStatusAwareWsMessage extends AbstractWsMessage implements StatusAwareMessage {

    /**
     * 消息投递时间
     */
    protected Date postTime;
    /**
     * 消息被读时间
     */
    protected Date readTime;

    /**
     * 消息状态
     */
    protected WsMessageEnum.WsMessageStatus messageStatus;

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public WsMessageEnum.WsMessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(WsMessageEnum.WsMessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public String getMessageId() {
        return this.getId();
    }
}
