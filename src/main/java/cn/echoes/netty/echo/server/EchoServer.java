package cn.echoes.netty.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * -------------------------------------
 * echo server
 * 1.设置端口值（抛出一个 NumberFormatException 如果该端口参数的格式不正确）
 * 2.呼叫服务器的 start() 方法
 * 3.创建 EventLoopGroup
 * 4.创建 ServerBootstrap
 * 5.指定使用 NIO 的传输 Channel
 * 6.设置 socket 地址使用所选的端口
 * 7.添加 EchoServerHandler 到 Channel 的 ChannelPipeline
 * 8.绑定的服务器;sync 等待服务器关闭
 * 9.关闭 channel 和 块，直到它被关闭
 * 10.关机的 EventLoopGroup，释放所有资源。
 * -------------------------------------
 * Created by liutao on 2017/4/5 上午11:15.
 */
public class EchoServer {
    public static void main(String[] args) throws InterruptedException {
        new EchoServer(8000).run(); // 1 2
    }

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(); // 3
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group) //4
                    .channel(NioServerSocketChannel.class) // 5
                    .localAddress(new InetSocketAddress(port)) //6
                    .childHandler(new ChannelInitializer<SocketChannel>() { //7
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync(); //8
            System.out.println(EchoServer.class.getName() + " started and listen on +" + future.channel());
            future.channel().closeFuture().sync(); //9
        } finally {
            group.shutdownGracefully().sync(); //10
        }
    }
}
