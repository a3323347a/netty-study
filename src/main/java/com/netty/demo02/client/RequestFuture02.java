package com.netty.demo02.client;

import com.netty.asyn.RequestFuture;
import com.netty.asyn.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author carl.zheng
 * @date 2022/6/15 10:29
 */
public class RequestFuture02 {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    public static Map<Long, RequestFuture02> futureMap = new ConcurrentHashMap<>();

    private long id; //每次请求id

    private Object request; //请求参数

    private Object result; //响应结果

    private long timeout = 5000; //超时时间，5秒

    public RequestFuture02() {
        id = ATOMIC_INTEGER.incrementAndGet();
        addFuture(this);
    }

    //把请求放入缓存中
    public static void addFuture(RequestFuture02 future) {
        futureMap.put(future.getId(), future);
    }

    //同步获取响应结果
    public Object get() {
        synchronized (this) {
            while (this.result == null) {
                try {
                    //主线程默认等待5秒，然后查看是否获取到结果
                    this.wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.result;
    }

    //异步线程将结果返回主线程
    public static void received(Response resp) {
        RequestFuture02 future = futureMap.remove(resp.getId());
        if (future != null) {
            future.setResult(resp.getResult());
        }
        synchronized (future) {
            //通知主线程
            future.notify();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
