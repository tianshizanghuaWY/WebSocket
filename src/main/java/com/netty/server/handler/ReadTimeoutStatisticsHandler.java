package com.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.*;

import java.util.concurrent.TimeUnit;

/**
 * 统计读空闲channel, 参考 @ReadTimeoutHandler，
 * 重写逻辑，不再关闭读空闲channel
 *
 * @author 千阳
 * @date 2018-08-09
 */
public class ReadTimeoutStatisticsHandler extends IdleStateHandler {
    private boolean closed;

    public ReadTimeoutStatisticsHandler(int timeoutSeconds) {
        this((long)timeoutSeconds, TimeUnit.SECONDS);
    }

    public ReadTimeoutStatisticsHandler(long timeout, TimeUnit unit) {
        super(timeout, 0L, 0L, unit);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    protected final void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        assert evt.state() == IdleState.READER_IDLE;

        this.readTimedOut(ctx);
    }

    private void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        if(!this.closed) {
            ctx.fireExceptionCaught(ReadTimeoutException.INSTANCE);
            ctx.close();
            this.closed = true;
        }
    }
}
