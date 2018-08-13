package com.netty.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.netty.constant.Constant;
import com.netty.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatchers;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;


/**
 *
 */
public class WsMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
		//客服端发送过来的消息
		String request =  msg.text();

		AbstractWsMessage mogoWsMessage;
		try{
			mogoWsMessage = transferJsonStrToObj(request);
		}catch (Exception e){
			throw new Exception("消息格式不正确");
		}

		if(mogoWsMessage == null){
			throw new Exception("消息格式不正确");
		}

		if(mogoWsMessage.getMsgBusinessType() == null){
			throw new Exception("消息格式不正确, 缺失 businessType");
		}

		System.out.println("服务端收到：" + mogoWsMessage);

		paddingMessageInfo(mogoWsMessage, channelHandlerContext);

		handlerMessage(mogoWsMessage, channelHandlerContext);
	}

	private AbstractWsMessage transferJsonStrToObj(String jsonStr){
		if(StringUtils.isEmpty(jsonStr)){
			return null;
		}

		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		String messageType = jsonObject.get("messageType") == null ? "" : jsonObject.get("messageType").toString();
		if(WsMessageEnum.WsMessageType.TEXT.name().equals(messageType)){
			return JSONObject.parseObject(jsonStr, MogoWsTextMessage.class);
		}else if(WsMessageEnum.WsMessageType.IMAGE.name().equals(messageType)){
			return JSONObject.parseObject(jsonStr, MogoWsImageMessage.class);
		}

		return null;
	}

	/**
	 * 填充消息的ip信息，当未登录情形时，就是靠IP来广发消息的
	 */
	private void paddingMessageInfo(AbstractWsMessage mogoWsMessage, ChannelHandlerContext ctx){
		if(mogoWsMessage == null || mogoWsMessage.getFrom() == null){
			return;
		}

		//填充IP信息
		WsMessageEndpoint from = mogoWsMessage.getFrom();
		if(StringUtils.isEmpty(from.getIp())){
			SocketAddress socketAddress = ctx.channel().remoteAddress();
			if(socketAddress instanceof InetSocketAddress){
				from.setIp(((InetSocketAddress) socketAddress).getAddress().getHostAddress());
			}else {
				from.setIp(ctx.channel().remoteAddress().toString());
			}
		}

		//填充时间信息
		mogoWsMessage.setSendTime(new Date());
	}

	private void handlerMessage(AbstractWsMessage mogoWsMessage, ChannelHandlerContext ctx){
		if(mogoWsMessage == null || mogoWsMessage.getTo() == null){
			return;
		}

		//客服消息，在发送的时候还不确定 to 是谁
		WsMessageEnum.WsMsgBusinessType msgBusinessType = mogoWsMessage.getMsgBusinessType();
		String channelKey = mogoWsMessage.getTo().channeKey();
		String channelKeyFrom = mogoWsMessage.getFrom().channeKey();
		String messageStr = JSONObject.toJSONString(mogoWsMessage);
		switch (msgBusinessType){
			case FIND :{
				if(!StringUtils.isEmpty(channelKey)){
					ChannelGroup channelGroup = Constant.channelGroupMap.get(channelKey);
					if(channelGroup != null && !channelGroup.isEmpty()){
						ChannelGroupFuture future = channelGroup.writeAndFlush(new TextWebSocketFrame(messageStr), ChannelMatchers.all(), false);

						//需要发送消息状态通知
						if(mogoWsMessage instanceof AbstractStatusAwareWsMessage && !StringUtils.isEmpty(mogoWsMessage.getId())){
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
						Constant.addOfflineMsg(mogoWsMessage, channelKey);
					}

				}

				//发给其它端(同一个用户可能在不同端打开了websocket连接)
				if(!StringUtils.isEmpty(channelKeyFrom)){
					ChannelGroup channelGroupSelf = Constant.channelGroupMap.get(channelKeyFrom);

					//发给其它端, 但是不能发给自己(该消息发送端)
					if(channelGroupSelf != null){
						//voidPromise=false -> 不需要知道发送的结果
						channelGroupSelf.writeAndFlush(new TextWebSocketFrame(messageStr), ChannelMatchers.isNot(ctx.channel()), true);
					}

				}
				break;
			}
			case SERVICE:{
				break;
			}
			default:break;
		}
	}
}
