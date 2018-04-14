package net;

import net.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Guan
 * @date Created on 2018/4/8
 */
public class NetServerManager {

    private static Logger logger = LoggerFactory.getLogger(NetServerManager.class);

    private ExecutorService acceptService ;

    public static void main(String[] atgs) throws InterruptedException {
        NetServerManager manager = new NetServerManager();
        manager.init();
        Thread.sleep(2000);
        manager.stop();

    }

    public void init() {
        acceptService = Executors.newSingleThreadExecutor();
        acceptService.execute(new Runnable() {
            @Override
            public void run() {
//                SocketServer.getInstance().start();
                NettyServer.start();
            }
        });

    }


    public void stop() {
        NettyServer.stop();

//        SocketServer.getInstance().stop();
        acceptService.shutdownNow();
        logger.info("acceptService 线程已经关闭...");

    }


//    public static

}
