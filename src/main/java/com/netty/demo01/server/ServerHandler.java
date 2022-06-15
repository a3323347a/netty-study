package com.netty.demo01.server;

import com.alibaba.fastjson.JSONObject;
import com.netty.asyn.RequestFuture;
import com.netty.asyn.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author carl.zheng
 * @date 2022/6/14 17:32
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 读取客户端发送的数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //经过解码器，msg为String类型
        RequestFuture requestFuture = JSONObject.parseObject(msg.toString(),RequestFuture.class);
        long id = requestFuture.getId();
        Response response = new Response();
        response.setId(id);
        response.setResult("服务端响应客户端的请求");
        //把响应结果返回给客户端
        ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
    }
}
