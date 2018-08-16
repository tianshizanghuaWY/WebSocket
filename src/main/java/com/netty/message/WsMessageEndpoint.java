package com.netty.message;

import org.springframework.util.StringUtils;

/**
 * websocket 地址, 模拟现实生活中，写信人/收信人
 *
 * @author 千阳
 * @date 2018-07-29
 */
public class WsMessageEndpoint {
    /**
     * 联系人id
     */
    private String ctctId;
    /**
     * 联系人类型
     */
    private String ctctIdSource;
    /**
     * 联系人姓名
     */
    private String name;
    /**
     * ip
     */
    private String ip;

    public String getCtctId() {
        return ctctId;
    }

    public void setCtctId(String ctctId) {
        this.ctctId = ctctId;
    }

    public String getCtctIdSource() {
        return ctctIdSource;
    }

    public void setCtctIdSource(String ctctIdSource) {
        this.ctctIdSource = ctctIdSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 通过地址信息获得消息要发往的 channelKey
     * @return
     */
    public String channeKey(){
        if(!StringUtils.isEmpty(ctctId) && !StringUtils.isEmpty(ctctIdSource)){
            return ctctIdSource + "_" + ctctId;
        }

        if(!StringUtils.isEmpty(ip)){
            return ip;
        }

        return null;
    }
}
