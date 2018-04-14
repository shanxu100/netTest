package net.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * a stateful server with a custom binary protocol
 *
 * @author Guan
 * @date 2017/9/12
 */
public final class NettyServer {

    private static final int TCP_PORT = 8888;
    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    /**
     * 业务线程池
     */
    private EventExecutorGroup bizGroup;


    //region 单例模式

    private NettyServer() {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBossGroup", true));
        workerGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("NettyServerWorkGroup", true));
        bizGroup = new DefaultEventExecutorGroup(4);
    }

    private static class NettyServerBuilder {
        public static NettyServer nettyServer = new NettyServer();
    }

    private static NettyServer getInstance() {
        return NettyServerBuilder.nettyServer;
    }
    //endregion


    //===========================================
    //region 开放调用的方法

    /**
     * 对外开放的调用：开启服务器
     */
    public static void start() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("StartNetty-Pool-%d").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0L
                , TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(1024), factory);
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                getInstance().startServer();
            }
        });
    }

    /**
     * 对外开放的调用：关闭服务器
     */
    public static void stop() {
        getInstance().stopServer();
    }

    /**
     * 测试
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        start();
    }

    //endregion
    //===========================================


    /**
     * 启动服务器
     */
    public void startServer() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer(bizGroup))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            Channel channel = b.bind(TCP_PORT).sync().channel();
            logger.info("NettyServer 成功启动");
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            bizGroup.shutdownGracefully();
        }
    }

    /**
     * 关闭服务器
     */
    public void stopServer() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        bizGroup.shutdownGracefully();
        logger.info("nettyServer 关闭成功");
    }

}