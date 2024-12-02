package com.kano.kanorpc.config;


import lombok.Data;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfigYml {

    /**
     * 名称
     */
    private String name = "kano-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

}
