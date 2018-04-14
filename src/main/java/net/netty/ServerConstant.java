package net.netty;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.websocket.WebSocketPoint;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Guan
 * @date 2017/9/22
 */
public class ServerConstant {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ServerConstant.class);
    /**
     * 保存所有 建立连接 的WChannel,是线程安全的
     */
    public static CopyOnWriteArraySet<SocketChannel> CHANNEL_ALL = new CopyOnWriteArraySet<>();

    //====================================================================================================


    //===============================增删改查============================


    public static void addChannel(SocketChannel socketChannel)
    {
        if (socketChannel!=null)
        {
            CHANNEL_ALL.add(socketChannel);
        }
    }



    /**
     * 断开连接时，在Set和Map中删除该channel
     *
     * @param channel
     */
    public static void removeChannel(SocketChannel channel) {
        logger.info("从 CHANNEL_ALL 中移除 Channel：" + channel.toString());
        CHANNEL_ALL.remove(channel);

    }

    //==============================统计、计数==================================

    /**
     * 获取所有Channel的数量
     *
     * @return
     */
    public static int getAllChannelCount() {
        return CHANNEL_ALL.size();
    }



}
