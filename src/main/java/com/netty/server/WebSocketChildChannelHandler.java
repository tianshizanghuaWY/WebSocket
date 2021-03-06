package com.netty.server;

import javax.annotation.Resource;

import com.netty.server.handler.IpCheckHandler;
import com.netty.server.handler.TextWebSocketFrameHandler;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

@Component("webSocketChildChannelHandler")
public class WebSocketChildChannelHandler extends ChannelInitializer<SocketChannel>{

	@Resource(name = "webSocketServerHandler")
	private ChannelHandler webSocketServerHandler;

	@Resource(name = "textWebSocketFrameHandler")
	private ChannelHandler textWebSocketFrameHandler;

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		if(!IpCheckHandler.removed()){
			ch.pipeline().addLast("ipCheck", new IpCheckHandler());
		}
		ch.pipeline().addLast("http-codec", new HttpServerCodec());
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
		ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
		ch.pipeline().addLast("handler", webSocketServerHandler);
		ch.pipeline().addLast("textWebSocketFrameHandler", textWebSocketFrameHandler);
	}

}
