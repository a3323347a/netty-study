package com.netty.asyn;

/**
 * @author carl.zheng
 * @date 2022/6/14 16:49
 */
public class Response {

    private long id;
    private Object result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
