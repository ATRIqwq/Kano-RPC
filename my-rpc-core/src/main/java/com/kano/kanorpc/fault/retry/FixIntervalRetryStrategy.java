package com.kano.kanorpc.fault.retry;

import com.github.rholder.retry.*;
import com.kano.kanorpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FixIntervalRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {


        Retryer<RpcResponse> retry = RetryerBuilder.<RpcResponse>newBuilder()
                //当出现Exception异常时重试
                .retryIfExceptionOfType(Exception.class)
                //选择等待重试策略
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                //重试停止策略
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                //重试工作：使用 withRetryListener 监听重试，每次重试时，除了再次执行任务外，还能够打印当前的重试次数。
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.hasException()){
                            log.info("重试次数 {}",attempt.getAttemptNumber());
                        }

                    }
                })
                .build();

            return retry.call(callable);

    }
}
