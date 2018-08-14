package com.netty.server.poster;

import com.alibaba.fastjson.JSONObject;
import com.netty.constant.Constant;
import com.netty.message.AbstractStatusAwareWsMessage;
import com.netty.message.AbstractWsMessage;
import com.netty.message.MogoStatusNotifyMessage;
import com.netty.message.WsMessageEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatchers;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 找房类消息投递器
 *
 * @author 千阳
 * @date 2018-08-14
 */
public class FindMessagePoster extends AbstractPoster{
    FindMessagePoster(AbstractWsMessage mogoWsMessage, ChannelHandlerContext ctx) {
        super(mogoWsMessage, ctx);
    }

    @Override
    protected void post() {
        if(mogoWsMessage == null || mogoWsMessage.getTo() == null){
            return;
        }

        System.out.println("投递消息:" + mogoWsMessage);

        WsMessageEnum.WsMsgBusinessType msgBusinessType = mogoWsMessage.getMsgBusinessType();
        String toChannelKey = mogoWsMessage.getTo().channeKey();
        String fromChannelKey = mogoWsMessage.getFrom().channeKey();
        String messageStr = JSONObject.toJSONString(mogoWsMessage);

        //1. 先发给其它端(同一个用户可能在不同端打开了websocket连接)
        if(!StringUtils.isEmpty(fromChannelKey)){
            ChannelGroup channelGroupSelf = Constant.channelGroupMap.get(fromChannelKey);

            //发给其它端, 但是不能发给自己(该消息发送端)
            if(channelGroupSelf != null){
                //voidPromise=true -> 不需要知道发送的结果
                channelGroupSelf.writeAndFlush(new TextWebSocketFrame(messageStr), ChannelMatchers.isNot(ctx.channel()), true);
            }
        }

        //2. 再投递消息
        if(!StringUtils.isEmpty(toChannelKey)){
            //获取消息 "去" ChannelGroup
            ChannelGroup channelGroup = Constant.channelGroupMap.get(toChannelKey);

            boolean needNotifyStatus = mogoWsMessage instanceof AbstractStatusAwareWsMessage && !StringUtils.isEmpty(mogoWsMessage.getId());
            if(channelGroup != null && !channelGroup.isEmpty()){
                ChannelGroupFuture future = channelGroup.writeAndFlush(new TextWebSocketFrame(messageStr), ChannelMatchers.all(), !needNotifyStatus);

                //需要发送消息状态通知
                if(needNotifyStatus){
                    AbstractStatusAwareWsMessage statusAwareWsMessage = (AbstractStatusAwareWsMessage) mogoWsMessage;

                    future.addListener(new GenericFutureListener<ChannelGroupFuture>(){
                        @Override
                        public void operationComplete(ChannelGroupFuture future) throws Exception {
                            if(future.isSuccess() || future.isPartialSuccess()){
                                //投递时间
                                statusAwareWsMessage.setPostTime(new Date());

                                //发送系统通知信息
                                MogoStatusNotifyMessage notifyMessage
                                        = MogoStatusNotifyMessage.buildNotifyMessage(statusAwareWsMessage, null);

                                String channelKeyTemp = notifyMessage.getTo().channeKey();
                                ChannelGroup channelGroup = Constant.channelGroupMap.get(channelKeyTemp);
                                String messageStr = JSONObject.toJSONString(notifyMessage);
                                if(channelGroup != null && !channelGroup.isEmpty()){
                                    channelGroup.writeAndFlush(new TextWebSocketFrame(messageStr), ChannelMatchers.all(), true);
                                }
                            }
                        }
                    });

                }

            }else{
                //记录离线消息
                Constant.addOfflineMsg(mogoWsMessage, toChannelKey);
            }
        }
    }

    @Override
    protected void saveMsg() {
        System.out.println("-----------> 保存 message");
    }
}
