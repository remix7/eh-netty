package cn.echoes.netty.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * -------------------------------------
 * <p>
 * -------------------------------------
 * Created by liutao on 2017/4/4 下午5:20.
 */
public class TimeClient {
    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 8000;

        EventLoopGroup work = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeClientHandle());
                        }
                    });
            //启动客户端
            ChannelFuture f = bootstrap.connect(host, port);
            //等待关闭连接
            f.channel().closeFuture().sync();
        } finally {
            work.shutdownGracefully().sync();
        }
    }
}
