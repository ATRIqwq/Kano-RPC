package com.kano.kanorpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.kano.RpcApplication;
import com.kano.kanorpc.config.RegistryConfig;
import com.kano.kanorpc.config.RpcConfig;
import com.kano.kanorpc.constant.RpcConstant;
import com.kano.kanorpc.loadbalancer.LoadBalancer;
import com.kano.kanorpc.loadbalancer.LoadBalancerFactory;
import com.kano.kanorpc.model.RpcRequest;
import com.kano.kanorpc.model.RpcResponse;
import com.kano.kanorpc.model.ServiceMetaInfo;
import com.kano.kanorpc.protocol.*;
import com.kano.kanorpc.registry.Registry;
import com.kano.kanorpc.registry.RegistryFactory;
import com.kano.kanorpc.serializer.JdkSerializer;
import com.kano.kanorpc.serializer.Serializer;
import com.kano.kanorpc.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
//            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
            //负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            //将调用方法名（请求路径）作为负载均衡参数
            Map<String,Object> requestParams = new HashMap<>();
            requestParams.put("methodName",rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);


//            发送HTTP请求
            try(
                    HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                            .body(bodyBytes)
                            .execute()
            ) {
                byte[] result = httpResponse.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }

            //发送TCP请求
//            Vertx vertx = Vertx.vertx();
//            NetClient netClient = vertx.createNetClient();
//            CompletableFuture<RpcResponse> responseFuture  = new CompletableFuture<>();
//
//            netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(), result -> {
//                if (result.succeeded()) {
//                    System.out.println("Connected to TCP server");
//                    io.vertx.core.net.NetSocket socket = result.result();
//
//                    //构造消息
//                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
//                    ProtocolMessage.Header header = new ProtocolMessage.Header();
//                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
//                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
//                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
//                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
//                    header.setRequestId(IdUtil.getSnowflakeNextId());
//
//                    protocolMessage.setHeader(header);
//                    protocolMessage.setBody(rpcRequest);
//
//                    // 编码请求
//                    try {
//                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
//                        socket.write(encodeBuffer);
//                    } catch (IOException e) {
//                        throw new RuntimeException("协议消息编码错误");
//                    }
//
//                    // 接收响应
//                    socket.handler(buffer -> {
//                        try {
//                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage  = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
//                            responseFuture.complete(rpcResponseProtocolMessage.getBody());
//                        } catch (IOException e) {
//                            throw new RuntimeException("协议消息解码错误");
//                        }
//
//                    });
//                } else {
//                    System.err.println("Failed to connect to TCP server");
//                }
//            });
//
//            // 阻塞，直到响应完成，才会继续向下执行
//            RpcResponse rpcResponse = responseFuture.get();
//            //关闭连接
//            netClient.close();
//            return rpcResponse.getData();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
