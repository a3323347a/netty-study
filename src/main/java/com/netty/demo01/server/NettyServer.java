package com.netty.demo01.server;

import com.netty.demo02.server.ServerHandler02;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author carl.zheng
 * @date 2022/6/14 17:14
 */
public class NettyServer {
    public static void main(String[] args) {
        //boss线程，只有一个线程，用来监听OP_ACCEPT事件
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //worker线程，默认线程数为CPU核数*2，监听客户连接的OP_WRITE和OP_READ事件，处理I/O事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            //设置TCP Socket通道，为NioServerSocketChannel
            serverBootstrap.channel(NioServerSocketChannel.class);
            //设置TCP参数，TCP窗口size为128
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128)
                    //当有客户端注册读写事件时，初始化Handler，并将Handler加入管道中
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            //把接收到的ByteBuf数据包转换成String
                            socketChannel.pipeline().addLast(new StringDecoder());
                            //socketChannel.pipeline().addLast(new ServerHandler()); demo01
                            socketChannel.pipeline().addLast(new ServerHandler02()); //demo02
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(4, false));
                            socketChannel.pipeline().addLast(new StringEncoder());
                        }
                    });
            //同步绑定端口
            ChannelFuture future = serverBootstrap.bind("127.0.0.1", 8090).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
