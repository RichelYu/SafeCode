package com.zzu.ybr.ybrsafety.util;

import com.ybr.Config;
import com.ybr.threading.ReqTaskThread;

/**
 * 程序开始前 初始化
 */
public class InitClient {
    /**
     * 初始化
     */
    public static void init(){
        loadConfig();
    }

    /**
     * 读取配置
     */
    private static void loadConfig(){
        Config.NETServerIp = "101.132.33.200"; // 设置服务器IP
        Config.NETListenPort = 26927; // 设置服务器端口  默认26927
    }

    /**
     * 初始化客户端线程
     */
    private static void initClientThread(){
        new Thread(new ReqTaskThread(), "ReqTaskThread").start(); //启动socket请求线程
    }
}
