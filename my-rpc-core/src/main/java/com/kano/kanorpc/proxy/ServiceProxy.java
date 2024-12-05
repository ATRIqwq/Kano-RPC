package com.kano.kanorpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kano.RpcApplication;
import com.kano.kanorpc.config.RegistryConfig;
import com.kano.kanorpc.config.RpcConfig;
import com.kano.kanorpc.constant.RpcConstant;
import com.kano.kanorpc.model.RpcRequest;
import com.kano.kanorpc.model.RpcResponse;
import com.kano.kanorpc.model.ServiceMetaInfo;
import com.kano.kanorpc.registry.Registry;
import com.kano.kanorpc.registry.RegistryFactory;
import com.kano.kanorpc.serializer.JdkSerializer;
import com.kano.kanorpc.serializer.Serializer;
import com.kano.kanorpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理（JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

//        //指定序列化器
//        JdkSerializer jdkSerializer = new JdkSerializer();
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());


        //构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();


        //发送请求，接收响应，然后反序列化返回
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            //获取RPC配置，创建注册中心实例，查询服务列表
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            //发送请求
            try(
                    HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                            .body(bodyBytes)
                            .execute()
            ) {
                byte[] result = httpResponse.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
