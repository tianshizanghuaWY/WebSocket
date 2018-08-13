package com.netty.business.task;

import com.netty.constant.Constant;
import com.netty.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.util.StringUtils;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-08-13
 */
public class CheckIpValidTask extends AbstractTask{

    @Override
    public void execute() {
        if(ctx == null){
            return;
        }

        //TODO 使用redis
        String ip = CommonUtil.getRemoteIpFromChannel(ctx);
        if(!StringUtils.isEmpty(ip) && Constant.blackIps.contains(ip)){
            System.out.println("IP在黑名单内, 强制退出该channel...");
            ctx.close();
        }
    }

    public CheckIpValidTask(ChannelHandlerContext ctx){
        super(ctx);
    }
}
