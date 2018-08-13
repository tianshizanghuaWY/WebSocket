package com.netty.message;

/**
 * Websockrt 消息枚举
 *
 * @author 千阳
 * @date 2018-07-29
 */
public class WsMessageEnum {

    /**
     * 消息类型
     */
    public enum WsMessageType {
        TEXT("text", "文本"), IMAGE("image", "图片"), VOICE("voice", "音频");

        private String type;
        private String desc;

        WsMessageType(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public String getType() {
            return type;
        }
    }

    /**
     * 消息类型
     */
    public enum WsMsgBusinessType {
        FIND("find", "找房信息"), SERVICE("service", "客服信息"), SYSTEM("system", "系统信息"), MESSAGE_STATE("messageStatus", "消息状态信息");

        private String type;
        private String desc;

        WsMsgBusinessType(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public String getType() {
            return type;
        }
    }

    /**
     * 消息状态
     */
    public enum WsMessageStatus {
        SEND("send", "已发送"), POSTED("posted", "已投递"), READ("read", "已读");

        private String type;
        private String desc;

        WsMessageStatus(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public String getType() {
            return type;
        }
    }
}
