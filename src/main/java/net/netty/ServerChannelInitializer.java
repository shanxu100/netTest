package net.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.TimeUnit;


/**
 * 作为server的childHandler，负责处理Server接收数据
 * <p>
 *
 * @author Guan
 * @date 2017/10/2
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private EventExecutorGroup bizGroup;


    public ServerChannelInitializer(EventExecutorGroup bizGroup) {
        this.bizGroup = bizGroup;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        /**
         * ChannelInboundHandler按照注册的先后顺序执行；ChannelOutboundHandler按照注册的先后顺序逆序执行，
         */
        pipeline.addLast("Ping", new IdleStateHandler(180, 0, 0, TimeUnit.SECONDS));


        pipeline.addLast(bizGroup, "ServerBusinessHandler", new ServerBusinessHandler());

    }


}
