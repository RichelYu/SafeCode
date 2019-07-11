package com.ybr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ybr.dbio.DBConnection;
import com.ybr.threading.ListenPortServer;
import com.ybr.threading.MailServer;
import com.ybr.threading.SystemManageThread;
import com.ybr.util.LogUtil;
import com.ybr.util.PropertiesLoader;
import com.ybr.util.SvrClassLoader;

/**
 * 启动服务
 * 
 * @author Administrator
 *
 */
public class Server {
	/**
	 * 启动服务
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// 读取配置文件数据
		PropertiesLoader.initPropertiesData();
		// 扫描注解类
		SvrClassLoader.init();
		// 数据库连接测试
		DBConnection.testDataBaseConnection();
		// 初始化 任务线程池 ..服务性质的线程不使用线程池维护
		VarCache.taskThreadPool = Executors.newFixedThreadPool(Config.MaxThreadNum);
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "ThreadPool init finish, max thread num: " + Config.MaxThreadNum);
		// 系统监视器 线程
		Thread manager = new Thread(new SystemManageThread(), "ServerManager");
		// 邮件发送 线程
		Thread mailSender = new Thread(new MailServer(), "MailSender");
		// 启动端口线程
		Thread portListener = new Thread(new ListenPortServer(Config.NETListenPort),
				"ListenPortThread" + Config.NETListenPort);
		manager.start();
		mailSender.start();
		portListener.start();
	}
}