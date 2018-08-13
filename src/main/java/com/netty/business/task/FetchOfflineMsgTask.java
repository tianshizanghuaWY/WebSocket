package com.netty.business.task;

import com.alibaba.fastjson.JSONObject;
import com.netty.constant.Constant;
import com.netty.message.AbstractWsMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Queue;
import java.util.Stack;

/**
 * 用户登陆时, 获取离线消息并发送
 *
 * @author 千阳
 * @date 2018-08-13
 */
public class FetchOfflineMsgTask extends AbstractTask{

    @Override
    public void execute() {
        if(ctx == null){
            return;
        }

        String channelKey = ctx.channel().attr(AttributeKey.valueOf(Constant.CHANNEL_KEY)).get().toString();
        if(StringUtils.isEmpty(channelKey)){
            return;
        }

        Queue<AbstractWsMessage> messageStack = Constant.fetchOfflineMsgs(channelKey);
        if(!CollectionUtils.isEmpty(messageStack)){
            while (true){
               AbstractWsMessage message = messageStack.poll();

               if(message == null){
                   break;
               }

               String messageStr = JSONObject.toJSONString(message);

               ctx.writeAndFlush(new TextWebSocketFrame(messageStr));

               //TODO 发送消息状态通知
            }
        }

        System.out.println("离线消息发送完毕");
    }

    public FetchOfflineMsgTask(ChannelHandlerContext ctx){
        super(ctx);
    }
}
