package com.netty.server.handler;

import com.netty.business.BusinessTaskExecutor;
import com.netty.business.task.AddBlackIpTask;
import com.netty.util.CommonUtil;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.springframework.stereotype.Component;

import com.netty.constant.Constant;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

/**
 * websocket 具体业务处理方法
 * @author admin
 * */

@Component("webSocketServerHandler")
@Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

	//TODO 需要确认有没有线程安全问题
	private WebSocketServerHandshaker handshaker;

	/**
	 * 当客户端连接成功，返回个成功信息
	 * */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}

	/**
	 * 当客户端断开连接
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	/**
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof WebSocketFrame){
			//WebSocketFrame 合法性校验
			webSocketFrameCheck(ctx, (WebSocketFrame)msg);

			if(msg instanceof TextWebSocketFrame){
				ctx.fireChannelRead(msg);
				return;
			}
		}

		//交由父方法处理,SimpleChannelInboundHandler 的 channelRead() 会做资源释放的处理
		super.channelRead(ctx, msg);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		if(msg instanceof FullHttpRequest){
			//http：//xxxx
			handleHttpRequest(ctx,(FullHttpRequest)msg);
		}else if(msg instanceof WebSocketFrame){
			//ws://xxxx
			webSocketFrameCheck(ctx,(WebSocketFrame)msg);
		}
	}

	/**
	 * 处理 websocket 消息
	 * @param ctx
	 * @param frame
	 * @throws Exception
	 */
	private void webSocketFrameCheck(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception{
		boolean needRelease = false;
		try{
			//关闭请求
			if(frame instanceof CloseWebSocketFrame){
				needRelease = true;
				handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
				return;
			}
			//ping请求
			if(frame instanceof PingWebSocketFrame){
				needRelease = true;
				ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
				return;
			}
			//只支持文本格式，不支持二进制消息
			if(!(frame instanceof TextWebSocketFrame)){
				needRelease = true;
				throw new UnsupportedOperationException("仅支持文本格式,当前格式:" + frame.getClass());
			}
		}finally {
			if(needRelease){
				ReferenceCountUtil.release(frame);
			}
		}
	}

	/**
	 * 对于每一次 ws 连接
	 * 第一次请求是http请求，请求头包括upgrade的信息, 也就是请求协议升级到 ws,在这里可以获得url，针对不同的业务场景
	 */
	public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req){
		System.out.println("---------> 处理http请求消息, 做协议升级处理");
		if(!req.decoderResult().isSuccess()){
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}

		// 非 ws 请求
		if(!"websocket".equals(req.headers().get("Upgrade"))) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}

		//token 校验
		boolean tokenValid = false;
		HttpMethod method = req.getMethod();
		String url = req.uri() == null ? "" : req.uri();
		String[] loginInfo = resolverCtctIdSouceFromUrl(url);
		String channelKey = CommonUtil.getRemoteIpFromChannel(ctx);
		if(loginInfo != null){
			channelKey = loginInfo[0] + "_" + loginInfo[1];
		}

		QueryStringDecoder decoder = new QueryStringDecoder(url);
		Map<String, List<String>> parameters = decoder.parameters();
		if(parameters != null && !CollectionUtils.isEmpty(parameters.get("token"))){
			String token = parameters.get("token").get(0);
			if(channelKey.equals(token)){
				tokenValid = true;
			}
		}
		if(!tokenValid){
			//失败次数记录
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));

			//添加IP到黑名单
			BusinessTaskExecutor.getInstance().addTask(new AddBlackIpTask(ctx));

			return;
		}

		WebSocketServerHandshakerFactory wsFactory
				= new WebSocketServerHandshakerFactory("ws:/" + ctx.channel()
				                                         + "/websocket",null,false);
		handshaker = wsFactory.newHandshaker(req);

		if(handshaker == null){
			//不支持
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		}else{
			//够造握手响应消息给客户端, 同时将WebSocket 相关的编解码类动态的添加到ChannelPipeline中.
			handshaker.handshake(ctx.channel(), req);
		}

		//最后注册 channel, 方便广发消息
		ctx.channel().attr(AttributeKey.valueOf(Constant.CHANNEL_KEY)).set(channelKey);
		Constant.registerChannel(ctx);
	}
	
	public static void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest req,DefaultFullHttpResponse res){
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
		
	}
	
    private static boolean isKeepAlive(FullHttpRequest req)
    {
        return false;
    }


	/**
	 * 异常处理，netty默认是关闭channel
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		//输出日志
		cause.printStackTrace();
		ctx.close();
	}

	/**
	 * 从 url 中解析出当前用户类型 ex: /1/234
	 * @param url
	 * @return
	 */
	private String[] resolverCtctIdSouceFromUrl(String url){
		if(StringUtils.isEmpty(url)){
			return null;
		}

		url = url.trim();
		QueryStringDecoder decoder = new QueryStringDecoder(url);

		int startIndex = 0;
		if(url.startsWith("/")){
			startIndex = 1;
		}

		url = url.indexOf("?") > 0 ? url.substring(startIndex, url.indexOf("?")) : url.substring(startIndex);

		String[] pathParams = url.split("/");
		if(pathParams == null || pathParams.length != 2){
			return null;
		}

		return pathParams;
	}
}
