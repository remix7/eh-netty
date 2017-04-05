package cn.echoes.netty.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * -------------------------------------
 * echo client
 * 1.创建 Bootstrap
 * 2.指定 EventLoopGroup 来处理客户端事件。由于我们使用 NIO 传输，所以用到了 NioEventLoopGroup 的实现
 * 3.使用的 channel 类型是一个用于 NIO 传输
 * 4.设置服务器的 InetSocketAddress
 * 5.当建立一个连接和一个新的通道时，创建添加到 EchoClientHandler 实例 到 channel pipeline
 * 6.连接到远程;等待连接完成
 * 7.阻塞直到 Channel 关闭
 * 8.调用 shutdownGracefully() 来关闭线程池和释放所有资源
 * <p>
 * -------------------------------------
 * Created by liutao on 2017/4/5 上午11:27.
 */
public class EchoClient {

    public static void main(String[] args) throws InterruptedException {
        new EchoClient("127.0.0.1", 8000).run();
    }

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap(); //1
            bootstrap.group(group) //2
                    .channel(NioSocketChannel.class) //3
                    .remoteAddress(new InetSocketAddress(host, port)) //4
                    .handler(new ChannelInitializer<SocketChannel>() { //5
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect().sync(); //6
            future.channel().closeFuture().sync(); //7
        } finally {
            group.shutdownGracefully().sync(); //8
        }
    }

}
