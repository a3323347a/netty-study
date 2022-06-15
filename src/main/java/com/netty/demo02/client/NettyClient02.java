package com.netty.demo02.client;

import com.alibaba.fastjson.JSONObject;
import com.netty.asyn.RequestFuture;
import com.netty.demo01.client.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * @author carl.zheng
 * @date 2022/6/15 10:03
 */
public class NettyClient02 {

    public static EventLoopGroup group;
    public static Bootstrap bootstrap;
    public static ChannelFuture future = null;

    static {
        //客户端启动辅助类
        bootstrap = new Bootstrap();
        //开启一个线程组
        group = new NioEventLoopGroup();
        //设置socket通道
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);

        //设置内存分配，重用缓冲区，TODO 目前不知道这个重用缓冲区是什么意思
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        final ClientHandler02 handler = new ClientHandler02();
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                /**
                 * LengthFieldPrepender与LengthFieldBasedFrameDecoder通常用来解决半包和粘包的问题
                 * 作用：将当前发送消息的二进制字节长度，添加到缓冲区头部；这样消息就有了固定长度，长度存储在缓冲头中
                 */
                channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                //把接收到的ByteBuf数据包转换成String
                channel.pipeline().addLast(new StringDecoder());
                //业务处理Handler
                channel.pipeline().addLast(handler);
                channel.pipeline().addLast(new LengthFieldPrepender(4, false));
                //把字符串消息转换成ByteBuf
                channel.pipeline().addLast(new StringEncoder(Charset.defaultCharset()));
            }
        });
        try {
            future = bootstrap.connect("127.0.0.1", 8090).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Object sendRequest(Object msg) {
        try {
            RequestFuture02 requestFuture = new RequestFuture02();
            requestFuture.setRequest(msg);
            String requestStr = JSONObject.toJSONString(requestFuture);
            future.channel().writeAndFlush(requestStr);
            //同步等待结果，只有当promise被赋值后，程序才会继续向下执行
            Object result = requestFuture.get();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        NettyClient02 client02 = new NettyClient02();
        for (int i = 0; i < 20; i++) {
            Object result = client02.sendRequest("hello");
            System.out.println(result);
        }
    }
}
