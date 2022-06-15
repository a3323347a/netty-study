package com.netty.demo01.client;

import com.alibaba.fastjson.JSONObject;
import com.netty.asyn.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;

/**
 * @author carl.zheng
 * @date 2022/6/14 17:53
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Promise<Response> promise;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = JSONObject.parseObject(msg.toString(), Response.class);
        promise.setSuccess(response);
    }

    public Promise<Response> getPromise() {
        return promise;
    }

    public void setPromise(Promise<Response> promise) {
        this.promise = promise;
    }
}
