package com.kano.kanorpc.protocol;

import lombok.Getter;

/**
 * 协议消息的状态枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;

    ProtocolMessageTypeEnum(int key){
        this.key = key;
    }

    /**
     * 根据 value 获取枚举值
     * @param key
     * @return
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key){
        for (ProtocolMessageTypeEnum anEum : ProtocolMessageTypeEnum.values()) {
            if (anEum.key == key){
                return anEum;
            }
        }
        return null;
    }
}
