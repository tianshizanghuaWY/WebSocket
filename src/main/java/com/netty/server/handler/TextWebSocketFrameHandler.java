package com.netty.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.netty.business.BusinessTaskExecutor;
import com.netty.message.*;
import com.netty.server.poster.IPoster;
import com.netty.server.poster.WsMsgPosterFactory;
import com.netty.util.CommonUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;


/**
 * TextWebSocketFrame 类型WebSocket消息处理器
 */
@Component("textWebSocketFrameHandler")
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
	//private GlobalEventExecutor executor = GlobalEventExecutor.INSTANCE;
	private BusinessTaskExecutor executor = BusinessTaskExecutor.getInstance();

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

		//异步投递消息
		IPoster poster = WsMsgPosterFactory.buildMsgPoster(mogoWsMessage, channelHandlerContext);
		if(poster != null){
			executor.addTask(poster);
		}
	}

	private AbstractWsMessage transferJsonStrToObj(String jsonStr){
		if(StringUtils.isEmpty(jsonStr)){
			return null;
		}

		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		String messageType = jsonObject.get("messageType") == null ? "" : jsonObject.get("messageType").toString();
		String businessType = jsonObject.get("msgBusinessType") == null ? "" : jsonObject.get("msgBusinessType").toString();
		if(MogoWsChatMessage.matched(messageType, businessType)){
			return JSONObject.parseObject(jsonStr, MogoWsChatMessage.class);
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
			from.setIp(CommonUtil.getRemoteIpFromChannel(ctx));
		}

		//填充时间信息
		mogoWsMessage.setSendTime(new Date());
	}
}
