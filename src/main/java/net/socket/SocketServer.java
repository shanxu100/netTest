package net.socket;

import net.websocket.WebSocketPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 服务器，传统的 阻塞IO
 */
public class SocketServer {
    //lalala
    private static final int PORT = 8888;
    private static final CopyOnWriteArraySet<ServiceForClient> All_SOCKET_SET = new CopyOnWriteArraySet<>();
    private static final int BUFFER_SIZE = 1024;
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);


    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private ExecutorService mExecutorService = null;


    public static SocketServer getInstance() {
        return InstanceBuilder.instance;
    }

    public SocketServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            mExecutorService = Executors.newCachedThreadPool();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        try {
            logger.info("服务器已启动...Server:" + serverSocket.getInetAddress().getHostAddress());

            //不断接收Client连接，创建多线程处理
            while (true) {
                clientSocket = serverSocket.accept();
                //start a new thread to handle the connection
                mExecutorService.execute(new ServiceForClient(clientSocket));
            }
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }


    }

    public void stop() {
//        logger.info("11111111");
        try {
            for (ServiceForClient client : All_SOCKET_SET) {
                logger.info("关闭连接。。。");
                client.closeSocket();
            }
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
                logger.info("关闭Socket...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
//            Thread.currentThread().interrupt();
        }

    }


    /**
     * 处理具体的socket
     */
    private static class ServiceForClient implements Runnable {
        private Socket socket;
        private BufferedReader in = null;


        public ServiceForClient(Socket socket) {
            this.socket = socket;
            try {
                //把客户端放入客户端集合中
                All_SOCKET_SET.add(this);
                //获取输入流
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //客户端只要一连到服务器
                logger.info("建立socket连接:" + socketToString(socket) + " 当前已连接socket数量: " + All_SOCKET_SET.size());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            try {
                while (true) {

                    char[] chars = new char[BUFFER_SIZE];
                    int readLength = 0;//read()会在这里阻塞。
                    if ((readLength = in.read(chars)) > 0) {
                        String sc = String.valueOf(chars, 0, readLength).trim();
                        logger.info("接收到client " + socket.getInetAddress() + " 发送的消息：" + sc);
                        sendMsg(socket, sc);
                        updateWebSocket(sc);

                    } else if (readLength == 0) {
                        System.out.println("数据读取完毕");
                        break;
                    } else if (readLength == -1) {
                        System.out.println("数据读取发生异常……");
                        break;
                    }

                }
            } catch (SocketException e) {
                System.out.println("client: " + socket.getInetAddress() + " 异常终止。");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSocket();
            }
            logger.info("Thread " + Thread.currentThread().getId() + " Terminated");
        }

        /**
         * 关闭Socket连接
         */
        public void closeSocket() {
            try {
                All_SOCKET_SET.remove(this);
                in.close();
                socket.close();
                logger.info("关闭socket:" + socketToString(socket) + " 当前已连接socket数量: " + All_SOCKET_SET.size());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }


    }

    /**
     * 给客户端都发送信息。
     */
    public static void sendMsg(Socket socket, String msg) {
        logger.info("socket返回消息:" + msg);
        if (socket != null && !socket.isClosed()) {
            try {
                PrintWriter pout = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                        true);
                pout.print(msg);
                pout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.error("socket未连接，发送消息失败:" + socketToString(socket));
        }
    }

    public static void updateWebSocket(String msg) {
        WebSocketPoint.updateAllClient(msg);
    }


    public static String socketToString(Socket socket) {
        if (socket == null) {
            return "socket is null";
        } else {
            return "Socket To String====" + socket.toString();
        }
    }

    private static class InstanceBuilder {
        public static SocketServer instance = new SocketServer();
    }

}