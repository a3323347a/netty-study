package com.netty.asyn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author carl.zheng
 * @date 2022/6/14 16:42
 */
public class FutureMain {
    public static void main(String[] args) {
        //请求列表
        List<RequestFuture> requestFutureList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            long id = i;
            RequestFuture future = new RequestFuture();
            future.setId(id);
            future.setRequest("Hello Word");
            RequestFuture.addFuture(future);
            requestFutureList.add(future);
            //模拟发送请求
            sendMsg(future);
            //模拟线程
            SubThread subThread = new SubThread(future);
            subThread.start();
        }
        for (RequestFuture requestFuture : requestFutureList) {
            Object result = requestFuture.get();
            //输出结果
            System.out.println(result.toString());
        }
    }

    private static void sendMsg(RequestFuture requestFuture) {
        System.out.println("客户端发送数据，请求id为：" + requestFuture.getId());
    }
}
