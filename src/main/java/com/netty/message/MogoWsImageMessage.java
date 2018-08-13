package com.netty.message;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.netty.util.JsonUtil;

/**
 * websocket message - TEXT
 *
 * @author 千阳
 * @date 2018-07-29
 */
public class MogoWsImageMessage extends AbstractStatusAwareWsMessage{

    public MogoWsImageMessage(){
        this.messageType = WsMessageEnum.WsMessageType.IMAGE;
    }

    @Override
    public WsMessageEnum.WsMessageType getMessageType() {
        if(this.messageType == null){
            this.messageType = WsMessageEnum.WsMessageType.IMAGE;
        }

        return this.messageType;
    }

    /**
     * 消息内容
     */
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return JsonUtil.object2JSON(this, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat});
    }
}
