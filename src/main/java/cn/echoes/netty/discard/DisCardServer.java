package cn.echoes.netty.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * -------------------------------------
 * 1. NioEventLoopGroup是用来处理I / O操作的多线程事件循环器，Netty提供了许多不同的EventLoopGroup的实现用来处理不同的传输。
 * 在这个例子中我们实现了一个服务端的应用，因此会有2第一个经常被叫做“老板”，用来接收进来的连接。
 * 第二个经常被叫做“工人”，用来处理已经被接收的连接，一旦“老板”接收到连接，就业把连接信息注册到“工作者”上。
 * 如何知道多少个线程已经被使用，如何映射到已经创建的通道上都需要依赖于EventLoopGroup的实现，并且可以通过构造函数来配置他们的关系。
 * 2. ServerBootstrap是一个启动NIO服务的辅助启动类。你可以在这个服务中直接使用频道，
 * 但是这会是一个复杂的处理过程，在很多情况下你并不需要这样做。
 * 3.这里我们指定使用NioServerSocketChannel类来举例说明一个新的通道如何接收进来的连接。
 * 4.渠道信息是一个特殊的处理类，他的目的是帮助使用者配置一个新的渠道。
 * 也许你想通过增加一些处理类比较DiscardServerHandler来配置一个新的渠道或者其对应的ChannelPipeline来实现你的网络程序。
 * 当你的程序变的复杂时，可能你会增加更多的处理类到pipline上，然后提取这些匿名类到最顶层的类上
 * 5.你可以设置这里指定的通道实现的配置参数。我们正在写一个TCP / IP的服务端，
 * 因此我们被允许设置套接字的参数选项比如tcpNoDelay和keepAlive。
 * 请参考ChannelOption和详细的ChannelConfig实现的接口文档以上可以对ChannelOption的有一个大概的认识。
 * 6.你关注过选项（）和childOption（）吗？option（）是提供给NioServerSocketChannel用来接收进来的连接
 * .childOption（）是提供给由父管道ServerChannel接收到的连接，在这个例子中也是NioServerSocketChannel。
 * 7.我们继续，下载的就是绑定端口然后启动服务。
 * 这里我们在机器上绑定了机器所有网卡上的8080端口当然现在你可以多次调用bind（）方法（基于不同绑定地址） 。
 * -------------------------------------
 * Created by liutao on 2017/4/4 下午4:42.
 */
public class DisCardServer {

    private int port;

    public DisCardServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        //NioEventLoopGroup 用来处理多线程IO的循环器
        EventLoopGroup boos = new NioEventLoopGroup(); // 1
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap(); // 2
            bootstrap.group(boos, work)
                    .channel(NioServerSocketChannel.class) // 3
                    .childHandler(new ChannelInitializer<SocketChannel>() {  // 4
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new DisCardServerHandle());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // 5
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 6
            //绑定端口 接收进来的连接
            ChannelFuture future = bootstrap.bind(port).sync(); // 7
            //等待服务器连接 socket关闭
            future.channel().closeFuture().sync();
        } finally {
            work.shutdownGracefully();
            boos.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8000;
        }
        new DisCardServer(port).run();
    }
}
