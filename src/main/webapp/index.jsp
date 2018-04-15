<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
    String path = request.getSession().getServletContext().getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
    String websocketPath = "ws://" + request.getServerName() + ":" + request.getServerPort() + path + "/websocket";
    out.println("Websocket 地址：" + websocketPath+"\n\n");
    out.println("查看连接信息："+basePath+"/log/getLogInfo");
%>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <meta name="keyword" content="">


</head>
<body>
<h4 id="text"></h4>


<script>
    var websocketurl = "<%=websocketPath%>";

    if ('WebSocket' in window) {
        websocket = new WebSocket(websocketurl);
    }
    else {
        alert('当前浏览器 Not support websocket')
    }
    websocket.onerror = function () {
        document.getElementById("text").innerHTML += "错误：" + event.data + "<br/>";
    };

    websocket.onopen = function () {
        document.getElementById("text").innerHTML += "连接已建立<br/>";
    }

    websocket.onmessage = function () {
        document.getElementById("text").innerHTML += event.data + "<br/>";
    }


</script>
</body>
</html>
