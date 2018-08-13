package com.netty.constant;

import com.netty.business.BusinessTaskExecutor;
import com.netty.business.task.FetchOfflineMsgTask;
import com.netty.message.AbstractStatusAwareWsMessage;
import com.netty.message.AbstractWsMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 常量池
 * @author admin
 */
public class Constant {
	public static final String CHANNEL_KEY = "CHANNEL_KEY";

	public static Set<String> blackIps = new HashSet();

	private static Map<String, Queue<AbstractWsMessage>> offlineMsgs = new HashMap();

	/**
	 * 存放channelGroup
	 * key = ctctIdSource + ctctId, 或者 ip信息
	 * 理想情况下认为一个客户端只维持一个websocket 连接
	 */
	public static Map<String, ChannelGroup> channelGroupMap = new ConcurrentHashMap<String, ChannelGroup>() ;

	public static void registerChannel(ChannelHandlerContext ctx){
		try {
			if(ctx == null){
				return;
			}

			String channelKey = ctx.channel().attr(AttributeKey.valueOf(CHANNEL_KEY)).get().toString();
			if(StringUtils.isEmpty(channelKey)){
				return;
			}

			System.out.println("注册channel:" + ctx);

			ChannelGroup channelGroup = channelGroupMap.get(channelKey);
			if(channelGroup == null){
				channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
				channelGroupMap.put(channelKey, channelGroup);
			}
			channelGroup.add(ctx.channel());

			BusinessTaskExecutor.getInstance().addTask(new FetchOfflineMsgTask(ctx));
			System.out.println(channelGroup.size());
		}catch (Exception e){
			System.out.println("");
		}
	}

	/**
	 * 移出channel, 貌似这个是多余的, DefaultChannelGroup 在添加 channel时,就注入了 'remove' listener,
	 * 这个listener 监听的是channel.closeFuture - channel close 后会触发 'remove' 去将 channel 从 group 中移除
	 * @param ctx
	 */
	private static void unregisterChannel(ChannelHandlerContext ctx){
		try {
			if(ctx == null){
				return;
			}

			String channelKey = ctx.channel().attr(AttributeKey.valueOf(CHANNEL_KEY)).get().toString();
			if(StringUtils.isEmpty(channelKey)){
				return;
			}

			System.out.println("移除 channel:" + ctx);

			ChannelGroup channelGroup = channelGroupMap.get(channelKey);
			if(channelGroup == null){
				return;
			}

			channelGroup.remove(ctx.channel());
		}catch (Exception e){
			System.out.println("");
		}
	}

	//TODO redis
	public static void addOfflineMsg(AbstractWsMessage awareWsMessage, String toChannelKey){
		System.out.println("保存离线消息.....");
		Queue<AbstractWsMessage> messages = offlineMsgs.get(toChannelKey);
		if(messages == null){
			messages = new LinkedList<>();
			offlineMsgs.put(toChannelKey, messages);
		}

		messages.add(awareWsMessage);
	}

	public static Queue<AbstractWsMessage> fetchOfflineMsgs(String channelKey){
		return offlineMsgs.get(channelKey);
	}
}
