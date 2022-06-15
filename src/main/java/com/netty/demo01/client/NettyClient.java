package com.netty.demo01.client;

import com.alibaba.fastjson.JSONObject;
import com.netty.asyn.RequestFuture;
import com.netty.asyn.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.nio.charset.Charset;

/**
 * @author carl.zheng
 * @date 2022/6/14 17:49
 */
public class NettyClient {

    public static EventLoopGroup group = null;
    public static Bootstrap bootstrap = null;

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        //设置内存分配器
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    public static void main(String[] args) {
        try {
            Promise<Response> promise = new DefaultPromise<>(group.next());
            final ClientHandler handler = new ClientHandler();
            handler.setPromise(promise);

            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                    nioSocketChannel.pipeline().addLast(new StringDecoder());
                    nioSocketChannel.pipeline().addLast(handler);
                    nioSocketChannel.pipeline().addLast(new LengthFieldPrepender(4,false));
                    nioSocketChannel.pipeline().addLast(new StringEncoder(Charset.defaultCharset()));
                }
            });
            ChannelFuture future = bootstrap.connect("127.0.0.1",8090).sync();
            RequestFuture requestFuture = new RequestFuture();
            requestFuture.setId(1);
            requestFuture.setRequest("Hello Word");
            String result = JSONObject.toJSONString(requestFuture);
            future.channel().writeAndFlush(result);

            Response response = promise.get();
            System.out.println(JSONObject.toJSONString(response));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
