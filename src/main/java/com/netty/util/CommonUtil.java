package com.netty.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-08-13
 */
public class CommonUtil {
    public static String getRemoteIpFromChannel(ChannelHandlerContext ctx){
        if(ctx == null || ctx.channel() == null){
            return null;
        }

        SocketAddress addressRemove = ctx.channel().remoteAddress();
        if(addressRemove instanceof InetSocketAddress){
            InetSocketAddress inetSocketAddress = (InetSocketAddress)addressRemove;
            return inetSocketAddress.getHostString();
        }

        return addressRemove.toString();
    }
}
