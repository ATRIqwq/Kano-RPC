package com.kano.kanorpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kano.kanorpc.model.RpcRequest;
import com.kano.kanorpc.model.RpcResponse;
import com.kano.kanorpc.serializer.JdkSerializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理（JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        JdkSerializer jdkSerializer = new JdkSerializer();

        //构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        //接收请求，然后反序列化返回
        try {
            byte[] bodyBytes = jdkSerializer.serialize(rpcRequest);
            try(
                    HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                            .body(bodyBytes)
                            .execute()
            ) {
                byte[] result = httpResponse.bodyBytes();
                RpcResponse rpcResponse = jdkSerializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
