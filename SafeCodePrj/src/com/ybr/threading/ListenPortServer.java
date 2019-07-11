package com.ybr.threading;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ybr.Constants;
import com.ybr.Config;
import com.ybr.VarCache;
import com.ybr.util.LogUtil;

public class ListenPortServer implements Runnable {

	public static String ipAddress;

	public int port;

	public static int timeout;
	
	static {
		ipAddress = Config.NETServerIp;
		timeout = Config.NETTimeOut;
	}

	public ListenPortServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.bind(new InetSocketAddress(Config.NETServerIp ,port));//(new InetSocketAddress(port)); update 20180713
			ssc.socket().setSoTimeout(Config.NETTimeOut);
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "开始监听端口->" +ssc.getLocalAddress().toString().substring(1));
			while (true) {
				VarCache.portListenState.put(port, true);
				SocketChannel sc = ssc.accept();
				sc.socket().setSoTimeout(Config.NETTimeOut);
				if (sc != null) 
					VarCache.taskThreadPool.execute(new TaskThread(sc));
			}
		} catch (Exception e) {
			VarCache.portListenState.put(port, false);
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, e);
			e.printStackTrace();
		}
	}
}
