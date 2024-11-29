package com.kano.example.provider;

import com.kano.RpcApplication;
import com.kano.example.common.service.UserService;
import com.kano.kanorpc.config.RpcConfig;
import com.kano.kanorpc.constant.RpcConstant;
import com.kano.kanorpc.registry.LocalRegistry;
import com.kano.kanorpc.server.VertxHttpServer;
import com.kano.kanorpc.utils.ConfigUtil;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {

        //RPC框架初始化
        RpcApplication.init();

        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 Web 服务
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
