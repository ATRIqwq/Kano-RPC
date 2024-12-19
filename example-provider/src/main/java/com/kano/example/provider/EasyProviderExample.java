package com.kano.example.provider;

import com.kano.RpcApplication;
import com.kano.example.common.service.UserService;
import com.kano.kanorpc.bootstrap.ProviderBootstrap;
import com.kano.kanorpc.config.RegistryConfig;
import com.kano.kanorpc.config.RpcConfig;
import com.kano.kanorpc.constant.RpcConstant;
import com.kano.kanorpc.model.ServiceMetaInfo;
import com.kano.kanorpc.model.ServiceRegisterInfo;
import com.kano.kanorpc.registry.LocalRegistry;
import com.kano.kanorpc.registry.Registry;
import com.kano.kanorpc.registry.RegistryFactory;
import com.kano.kanorpc.server.VertxHttpServer;
import com.kano.kanorpc.tcp.VertxTcpServer;
import com.kano.kanorpc.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {

        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);
        ProviderBootstrap.init(serviceRegisterInfoList);

        // 启动 Web 服务
//        VertxHttpServer vertxHttpServer = new VertxHttpServer();
//        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
