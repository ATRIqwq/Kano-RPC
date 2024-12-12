package com.kano;

import com.kano.kanorpc.config.RegistryConfig;
import com.kano.kanorpc.config.RpcConfig;
import com.kano.kanorpc.constant.RpcConstant;
import com.kano.kanorpc.registry.EtcdRegistry;
import com.kano.kanorpc.registry.Registry;
import com.kano.kanorpc.registry.RegistryFactory;
import com.kano.kanorpc.serializer.Serializer;
import com.kano.kanorpc.serializer.SerializerFactory;
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

        //序列化器初始化
        SerializerFactory.getInstance(newRcpconfig.getSerializer());

        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("register init, config = {}",registryConfig);

        // 创建并注册 Shutdown Hook，JVM 退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
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
