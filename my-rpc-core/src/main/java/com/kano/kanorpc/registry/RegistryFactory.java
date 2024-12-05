package com.kano.kanorpc.registry;

import com.kano.kanorpc.spi.SpiLoader;

public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }


    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTER = new EtcdRegistry();


    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Registry getInstance(String key){
       return SpiLoader.getInstance(Registry.class,key);
    }


}
