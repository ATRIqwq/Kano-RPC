package com.kano.kanorpc.tcp;

import com.kano.kanorpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;


/**
 * 实现 TCP 服务器
 */
@Slf4j
public class VertxTcpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        //创建 vert.x 实例
        Vertx vertx = Vertx.vertx();

        //创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        server.connectHandler(new TcpServerHandler());

        // 启动 TCP 服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port " + port);
            } else {
                log.info("Failed to start TCP server: " + result.cause());
            }
        });
    }

//    public static void main(String[] args) {
//        new VertxTcpServer().doStart(8888);
//    }
}
