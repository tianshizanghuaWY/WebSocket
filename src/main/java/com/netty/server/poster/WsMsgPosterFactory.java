package com.netty.server.poster;

import com.netty.message.AbstractWsMessage;
import com.netty.message.WsMessageEnum;
import io.netty.channel.ChannelHandlerContext;

/**
 * 消息投递器 - 工厂类
 *
 * @author 千阳
 * @date 2018-08-14
 */
public class WsMsgPosterFactory {

    public static IPoster buildMsgPoster(AbstractWsMessage message, ChannelHandlerContext ctx){
        if(message == null || ctx == null){
            return null;
        }

        WsMessageEnum.WsMsgBusinessType businessType = message.getMsgBusinessType();
        switch (businessType){
            case FIND: return new FindMessagePoster(message, ctx);
            default:return null;
        }
    }
}
