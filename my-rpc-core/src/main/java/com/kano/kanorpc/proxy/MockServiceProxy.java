package com.kano.kanorpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理（JDK动态代理）
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {


    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 根据方法获取返回值类型
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}",method.getName());
        return getDefaultObject(methodReturnType);

    }

    /**
     * 生成指定类型的默认值对象
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        //判断TYPE是否为原始类型
        if (type.isPrimitive()){
            if (type == boolean.class){
                return false;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            }

        }
        //如果不是基本类型返回null
        return null;
    }


}
