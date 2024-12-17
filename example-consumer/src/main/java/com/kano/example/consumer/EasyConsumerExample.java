package com.kano.example.consumer;


import com.kano.example.common.model.User;
import com.kano.example.common.service.UserService;
import com.kano.kanorpc.config.RpcConfig;
import com.kano.kanorpc.proxy.MockServiceProxy;
import com.kano.kanorpc.proxy.ServiceProxyFactory;
import com.kano.kanorpc.utils.ConfigUtil;

/**
 * 简易服务消费者示例
 */
public class EasyConsumerExample {

    public static void main(String[] args) {

//        RpcConfig rpc = ConfigUtil.loadConfig(RpcConfig.class, "rpc");
//        System.out.println(rpc);

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
//        UserService mockProxy = ServiceProxyFactory.getMockProxy(UserService.class);

        // todo 需要获取 UserService 的实现类对象
        User user = new User();
        user.setName("yupi");

//        User user2 = new User();
//        user2.setName("saber");
//
//        User user3 = new User();
//        user3.setName("lancer");
        // 调用
        User newUser = userService.getUser(user);

//        User newUser2 = userService.getUser(user2);
//        User newUser3 = userService.getUser(user3);

        if (newUser != null) {
            System.out.println(newUser.getName());
//            System.out.println(newUser2.getName());
//            System.out.println(newUser3.getName());
        } else {
            System.out.println("user == null");
        }

    }


}