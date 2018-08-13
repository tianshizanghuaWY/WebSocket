package com.netty.business.task;

import com.netty.constant.Constant;
import com.netty.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-08-13
 */
public class AddBlackIpTask extends AbstractTask{

    @Override
    public void execute() {
        if(ctx == null){
            return;
        }

        //TODO 使用redis
        String ip = CommonUtil.getRemoteIpFromChannel(ctx);
        System.out.println("添加IP到黑名单...");
        Constant.blackIps.add(ip);

        ctx.close();
    }

    public AddBlackIpTask(ChannelHandlerContext ctx){
        super(ctx);
    }
}
