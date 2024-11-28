package com.kano.example.consumer;


import com.kano.example.common.model.User;
import com.kano.example.common.service.UserService;
import com.kano.kanorpc.proxy.ServiceProxyFactory;

/**
 * 简易服务消费者示例
 */
public class EasyConsumerExample {

    public static void main(String[] args) {

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        // todo 需要获取 UserService 的实现类对象
        User user = new User();
        user.setName("yupi");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}