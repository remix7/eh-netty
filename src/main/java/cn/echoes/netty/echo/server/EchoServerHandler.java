package cn.echoes.netty.echo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * -------------------------------------
 * echo 服务端 handler
 * 1.@Sharable 标识这类的实例之间可以在 channel 里面共享
 * 2.日志消息输出到控制台
 * 3.将所接收的消息返回给发送者。注意，这还没有冲刷数据
 * 4.冲刷所有待审消息到远程节点。关闭通道后，操作完成
 * 5.打印异常堆栈跟踪
 * 6.关闭通道
 * -------------------------------------
 * Created by liutao on 2017/4/5 上午11:10.
 */
@ChannelHandler.Sharable // 1
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 接收消息时调用执行
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("SERVER RECEIVED: \n" + buf.toString(CharsetUtil.UTF_8)); // 2
        ctx.write(buf); // 3
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 4
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(); //5
        ctx.close(); // 6
    }
}
