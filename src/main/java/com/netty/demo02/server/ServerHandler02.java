package com.netty.demo02.server;

import com.alibaba.fastjson.JSONObject;
import com.netty.asyn.Response;
import com.netty.demo02.client.RequestFuture02;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author carl.zheng
 * @date 2022/6/15 10:36
 */
public class ServerHandler02 extends ChannelInboundHandlerAdapter {

    /**
     * 读取客户端发送的数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //经过解码器，msg为String类型
        RequestFuture02 requestFuture = JSONObject.parseObject(msg.toString(), RequestFuture02.class);
        long id = requestFuture.getId();
        Response response = new Response();
        response.setId(id);
        response.setResult("服务端响应客户端的请求，id:" + id);
        //把响应结果返回给客户端
        ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
    }
}
