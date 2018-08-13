package com.netty.server;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 启动服务
 * @author admin
 */
public class WebSocketServer implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private EventLoopGroup bossGroup;
	
	@Autowired
	private EventLoopGroup workerGroup;
	
	@Autowired
	private ServerBootstrap serverBootstrap;

	@Resource(name="webSocketChildChannelHandler")
	private ChannelHandler childChannelHandler;
	
	private ChannelFuture channelFuture;
	
	private int port = 8181;
	
	public WebSocketServer(){
		System.out.println("初始化");
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			bulid(port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void bulid(int port) throws Exception{

		try {
			System.out.print("服务启动......");
			//（1）boss辅助客户端的tcp连接请求  worker负责与客户端之前的读写操作
			//（2）配置客户端的channel类型
			//(3)配置TCP参数，握手字符串长度设置
			//(4)TCP_NODELAY是一种算法，为了充分利用带宽，尽可能发送大块数据，减少充斥的小块数据，true是关闭，可以保持高实时性,若开启，减少交互次数，但是时效性相对无法保证
			//(5)开启心跳包活机制，就是客户端、服务端建立连接处于ESTABLISHED状态，超过2小时没有交流，机制会被启动
			//(6)netty提供了2种接受缓存区分配器，FixedRecvByteBufAllocator是固定长度，但是拓展，AdaptiveRecvByteBufAllocator动态长度
			//(7)绑定I/O事件的处理类,WebSocketChildChannelHandler中定义
			serverBootstrap.group(bossGroup,workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					//.option(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(592048))
					.childHandler(childChannelHandler);


			channelFuture = serverBootstrap.bind(port).sync();
			System.out.println("成功!");

			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	@PreDestroy
	public void close(){
		System.out.println("服务关闭.....");
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	public ChannelHandler getChildChannelHandler() {
		return childChannelHandler;
	}

	public void setChildChannelHandler(ChannelHandler childChannelHandler) {
		this.childChannelHandler = childChannelHandler;
	}

	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}

	public void setChannelFuture(ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
