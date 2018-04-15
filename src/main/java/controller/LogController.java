package controller;

import io.netty.channel.Channel;
import net.netty.ServerConstant;
import net.websocket.WebSocketPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.ReadFromFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Guan
 */
@Controller
@RequestMapping("log")
public class LogController {


    @RequestMapping("/getLogInfo")
    @ResponseBody
    public void getCardClientInfo(HttpServletRequest req, HttpServletResponse rep) throws Exception {
        req.setCharacterEncoding("UTF-8");
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n=======所有连接到服务器的Client信息=============\n\n");
        sb.append("size = " + ServerConstant.CHANNEL_ALL.size() + "\n");
        for (Channel channel : ServerConstant.CHANNEL_ALL) {
            sb.append("ClientInfo: " + channel.remoteAddress().toString() + "\n");
        }
        sb.append("==========" + createDate() + "==========");
        sb.append("\n\n=======所有连接到服务器的WebSocket Client信息=============\n\n");
        sb.append("size = " + WebSocketPoint.getOnlineCount() + "\n");
        sb.append(WebSocketPoint.getWSInfo());
        sb.append("==========" + createDate() + "==========\n\n");
        sb.append("====================END==================");

        rep.setContentType("application/json;charset=UTF-8");
        rep.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter writer = rep.getWriter();

        writer.print(sb.toString());
    }


    @RequestMapping("/getAllLog")
    @ResponseBody
    public void getAllLog(HttpServletRequest req, HttpServletResponse rep) throws Exception {
        req.setCharacterEncoding("UTF-8");
        String num = req.getParameter("num");
        StringBuilder sb = new StringBuilder();
        String filePath = "/var/log/netTest.log";
        File logFile = new File(filePath);
        if (num == null) {
            num = "50";
        }
        System.err.println("num=" + num + "file=" + logFile.getPath() + " " + logFile.toString());
        List<String> logs = ReadFromFile.readLastNLine(logFile, Integer.parseInt(num));
        for (String s : logs) {
            sb.append(s);
            sb.append("\n\n");
        }
        rep.setContentType("application/json;charset=UTF-8");
        rep.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter writer = rep.getWriter();
        writer.print(sb.reverse().toString());
    }

    public String createDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
        return dateFormat.format(new Date());
    }


}
