package com.netty.asyn;

/**
 * @author carl.zheng
 * @date 2022/6/14 16:49
 */
public class SubThread extends Thread {

    private RequestFuture future;

    public SubThread(RequestFuture future) {
        this.future = future;
    }

    @Override
    public void run() {
        Response response = new Response();
        response.setId(future.getId());
        response.setResult("server response，响应请求id：" + future.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        RequestFuture.received(response);
    }
}
