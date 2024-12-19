package com.kano.kanorpc.bootstrap;

import com.kano.RpcApplication;
import com.kano.kanorpc.config.RegistryConfig;
import com.kano.kanorpc.config.RpcConfig;
import com.kano.kanorpc.model.ServiceMetaInfo;
import com.kano.kanorpc.model.ServiceRegisterInfo;
import com.kano.kanorpc.registry.LocalRegistry;
import com.kano.kanorpc.registry.Registry;
import com.kano.kanorpc.registry.RegistryFactory;
import com.kano.kanorpc.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {

    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        //RPC框架初始化
        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }


        // 启动 Web 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
