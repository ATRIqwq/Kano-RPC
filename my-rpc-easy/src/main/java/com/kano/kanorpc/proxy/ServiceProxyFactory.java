package com.kano.kanorpc.proxy;


import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        /**
         * loader: 用哪个类加载器去加载代理对象
         *
         * interfaces:动态代理类需要实现的接口
         *
         * h:动态代理方法在执行时，会调用h里面的invoke方法去执行
         */
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
