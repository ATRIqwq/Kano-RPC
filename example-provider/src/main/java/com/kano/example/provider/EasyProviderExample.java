package com.kano.example.provider;

import com.kano.example.common.service.UserService;
import com.kano.kanorpc.registry.LocalRegistry;
import com.kano.kanorpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 提供服务
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8080);
    }
}
