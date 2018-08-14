package com.netty.server.poster;

import com.netty.message.AbstractWsMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author 千阳
 * @date 2018-08-14
 */
public abstract class AbstractPoster implements IPoster{
    protected AbstractWsMessage mogoWsMessage;
    protected ChannelHandlerContext ctx;
    AbstractPoster(AbstractWsMessage mogoWsMessage, ChannelHandlerContext ctx){
        this.mogoWsMessage = mogoWsMessage;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        try{
            Thread.sleep(2000);
            post();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            saveMsg();
        }
    }

    /**
     * 投递消息
     */
    protected abstract void post();

    /**
     * 保存消息入DB
     */
    protected abstract void saveMsg();
}
