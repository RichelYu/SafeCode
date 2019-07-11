package com.ybr.util;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ybr.Constants;

public class LogUtil {

	static {
		try {
			File f = new File("./log4j.properties");
			FileInputStream fin = new FileInputStream(f);
			Properties properties = new Properties();
			properties.load(fin);
			PropertyConfigurator.configure(properties);
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 日志等级
	 */
	private static int LOG_LEVEL = Constants.LOG_LEVEL_DEBUG;

	/**
	 * 日志输出对象
	 */
	private static Logger logger = Logger.getLogger(LogUtil.class);

	/**
	 * 写日志
	 * 
	 * @param level
	 * @param msg
	 */
	public static void WriteLog(int level, Exception e) {
		if (level > LOG_LEVEL)
			return;
		StringBuffer msg = new StringBuffer();
		msg.append("\n" + e.toString());
		for (StackTraceElement stackTraceElement : e.getStackTrace()) {
			msg.append("\n" + stackTraceElement);
		}
		WriteLog(level, msg.toString());
	}

	/**
	 * 写日志
	 * 
	 * @param level
	 * @param msg
	 */
	public static void WriteLog(int level, String msg) {
		msg = "<" + Thread.currentThread().getName() + "> " + msg;
		if (level > LOG_LEVEL)
			return;
		switch (level) {
		case Constants.LOG_LEVEL_DEBUG:
			logger.debug(msg);
			break;
		case Constants.LOG_LEVEL_INFO:
			logger.info(msg);
			break;
		case Constants.LOG_LEVEL_WARNING:
			logger.warn(msg);
			break;
		case Constants.LOG_LEVEL_ERROR:
			logger.error(msg);
			break;
		case Constants.LOG_LEVEL_FATAL:
			logger.fatal(msg);
			System.exit(-1);
			break;
		default:
			break;
		}
	}

	/**
	 * 读取配置后设置 输出日志等级
	 * 
	 * @param level
	 */
	public static void setLogLevel(int level) {
		LOG_LEVEL = level;
	}
}
