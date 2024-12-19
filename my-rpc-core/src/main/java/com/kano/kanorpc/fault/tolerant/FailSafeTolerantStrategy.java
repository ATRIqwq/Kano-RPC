package com.kano.kanorpc.fault.tolerant;

import com.kano.kanorpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默异常处理异常 -容错策略
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默异常处理，记录异常 {}", e);
        return new RpcResponse();
    }
}
