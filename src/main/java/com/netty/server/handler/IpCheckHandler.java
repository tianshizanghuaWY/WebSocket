package com.netty.server.handler;

import com.netty.business.BusinessTaskExecutor;
import com.netty.business.task.CheckIpValidTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 对IP进行黑名单校验
 *
 * @author 千阳
 * @date 2018-08-13
 */
public class IpCheckHandler extends ChannelInboundHandlerAdapter{
    private static volatile boolean removed = true;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(removed){
            ctx.channel().pipeline().remove("ipCheck");
        }else{
            BusinessTaskExecutor.getInstance().addTask(new CheckIpValidTask(ctx));
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(removed){
            ctx.channel().pipeline().remove("ipCheck");
        }else{
            BusinessTaskExecutor.getInstance().addTask(new CheckIpValidTask(ctx));
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----------> channelInactive");

        super.channelInactive(ctx);
    }

    public static boolean removed() {
        return removed;
    }
}
