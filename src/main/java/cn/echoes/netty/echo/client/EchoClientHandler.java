package cn.echoes.netty.echo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * -------------------------------------
 * echo client handler
 * 1.@Sharable标记这个类的实例可以在 channel 里共享
 * 2.当被通知该 channel 是活动的时候就发送信息
 * 3.记录接收到的消息
 * 4.记录日志错误并关闭 channel
 * -------------------------------------
 * Created by liutao on 2017/4/5 上午11:21.
 */
@ChannelHandler.Sharable // 1
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("netty rocks", CharsetUtil.UTF_8)); // 2
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("client received:" + byteBuf.toString(CharsetUtil.UTF_8)); //3
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception { //4
        cause.printStackTrace();
        ctx.close();
    }
}
