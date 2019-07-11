package com.ybr;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.ybr.entity.ent.Mail;
import com.ybr.entity.ent.UserSafeCode;

public class VarCache {

	/**
	 * DealCode -> DealBase 根据交易码获取 对应的处理类路径
	 */
	public static HashMap<String, String> baseDealMap = new HashMap<String, String>();

	/**
	 * 不进行登录检查的命令
	 */
	public static HashSet<String> noLoginCmdSet = new HashSet<String>();
	
	/**
	 * 端口状态 Map
	 */
	public static HashMap<Integer, Boolean> portListenState = new HashMap<Integer, Boolean>();
	
	/**
	 * 用户登录表  UID:RootID
	 */
	public static HashMap<String, String> loginMap = new HashMap<String, String>();
	
	/**
	 * 登录用户缓存表
	 */
	public static HashMap<String, UserSafeCode> userMap = new HashMap<String, UserSafeCode>();
	
	/**
	 * 发送邮箱队列
	 */
	public static LinkedBlockingQueue<Mail> mailQueue = new LinkedBlockingQueue<>();
	
	/**
	 * 请求线程池  需要在Server类中启动
	 */
	public static ExecutorService taskThreadPool = null; //Executors.newFixedThreadPool(Config.MaxThreadNum);
	
	/**
	 * DB工具静态连接  常连接  禁止关闭!!!
	 */
	public static Connection UtilDBConn = null;
}
