package com.netty.business.task;

import io.netty.channel.ChannelHandlerContext;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-08-13
 */
public abstract class AbstractTask implements ITask{
    protected ChannelHandlerContext ctx;

    public AbstractTask(ChannelHandlerContext ctx){
        this.ctx = ctx;
    }
}
