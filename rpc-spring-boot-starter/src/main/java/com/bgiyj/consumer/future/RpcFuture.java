package com.bgiyj.consumer.future;

import com.bgiyj.core.common.entity.RpcRequest;
import com.bgiyj.core.common.entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

@Slf4j
public class RpcFuture implements Future<Object> {
    private RpcRequest rpcRequest ;
    private RpcResponse response;
    private long startTime;
    private long responseTimeThreshold = 1;
    private ConsumerSynchronizer consumerSynchronizer;

    public RpcFuture(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
        consumerSynchronizer = new ConsumerSynchronizer();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isDone() {
        return consumerSynchronizer.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        consumerSynchronizer.acquire(ConsumerSynchronizer.STATUS_DONE);
        if (this.response != null) {
            return this.response;
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = consumerSynchronizer.tryAcquireNanos(1, unit.toNanos(timeout));
        if (success) {
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.rpcRequest.getId()
                    + ". Request class name: " + this.rpcRequest.getClassName()
                    + ". Request method: " + this.rpcRequest.getMethodName());
        }
    }

    /**
     * 任务调用完成
     * @param response
     */
    public void done(RpcResponse response) {
        consumerSynchronizer.release(ConsumerSynchronizer.STATUS_DONE);
        this.response = response;
        //计算执行时间
        long responseTime = System.currentTimeMillis() - startTime;
        //打印执行时间过长的请求
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = " + response.getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    static class ConsumerSynchronizer extends AbstractQueuedSynchronizer {
        private static final int STATUS_DONE = 1 ;
        private static final int STATUS_PENDING = 0;

        private static final long serialVersionUID = 1L;


        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == STATUS_DONE;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == STATUS_PENDING) {
                if (compareAndSetState(STATUS_PENDING, STATUS_DONE)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        protected boolean isDone() {
            return getState() == STATUS_DONE;
        }
    }
}
