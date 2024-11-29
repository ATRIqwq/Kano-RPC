package com.kano;

import com.kano.kanorpc.config.RpcConfig;
import com.kano.kanorpc.constant.RpcConstant;
import com.kano.kanorpc.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * RPC 框架应用
 * 相当于 holder，存放了项目全局用到的变量。双检锁单例模式实现
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * 支持传入自定义配置
     * @param newRcpconfig
     */
    public static void init(RpcConfig newRcpconfig){
        rpcConfig = newRcpconfig;
        log.info("rpc init, config = {}",newRcpconfig.toString());
    }

    /**
     * 初始化
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try {
            //读配置文件中，转换为对象
            newRpcConfig = ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            //配置失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 双检锁单例模式实现获取配置
     * @return
     */
    public static RpcConfig getRpcConfig(){
        if (rpcConfig == null){
            synchronized (RpcApplication.class){
                if (rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }


}
