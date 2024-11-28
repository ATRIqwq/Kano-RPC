package com.kano.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kano.example.common.model.User;
import com.kano.example.common.service.UserService;
import com.kano.kanorpc.model.RpcRequest;
import com.kano.kanorpc.model.RpcResponse;
import com.kano.kanorpc.serializer.JdkSerializer;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {


    @Override
    public User getUser(User user) {
        //1.将请求序列化
        //2.HTTP发送请求
        //3.将响应反序列化


        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();


        JdkSerializer jdkSerializer = new JdkSerializer();
        try {
            byte[] bodyBytes  = jdkSerializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = jdkSerializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
