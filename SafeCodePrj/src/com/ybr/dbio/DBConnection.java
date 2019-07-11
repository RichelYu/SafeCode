package com.ybr.dbio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.ybr.Constants;
import com.ybr.Config;
import com.ybr.VarCache;
import com.ybr.util.LogUtil;

/**
 * 数据库连接
 * 
 * @author Administrator
 *
 */
public class DBConnection {

	/**
	 * 数据库地址
	 */
	private static String DBURL = "";

	/**
	 * 数据库驱动
	 */
	private static String DBDRIVER = "";

	/**
	 * 数据库用户名
	 */
	private static String DBUSERNAME = "";

	/**
	 * 数据库密码
	 */
	private static String DBPASSWORD = "";

	/**
	 * 数据是否初始化完成 true 初始化完成 false 未初始化数据
	 */
	private static boolean hasData = false;

	/**
	 * 初始化变量 静态代码块
	 */
	static {
		loadVar();
	}

	/**
	 * 设置工具数据库连接 静态代码块
	 */
	static {
		VarCache.UtilDBConn = getConn();
	}

	/**
	 * 加载类 静态代码块
	 */
	static {
		try {
			Class.forName(DBDRIVER);
		} catch (ClassNotFoundException e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, e);
		}
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		int i = 0;
		Connection conn = null;
		while (conn == null && i++ < 10)
			conn = getConn();
		if (conn == null)
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, "取得连接次数过多");
		return conn;
	}

	/**
	 * 取得数据库连接
	 */
	private static Connection getConn() {
		Connection conn = null;
		if (!hasData)
			loadVar();
		try {
			conn = (Connection) DriverManager.getConnection(DBURL, DBUSERNAME, DBPASSWORD);
			conn.setAutoCommit(false);
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, e);
		}
		return conn;
	}

	/**
	 * 初始化私有静态变量
	 */
	private static void loadVar() {
		DBURL = Config.DBUrl;
		DBDRIVER = Config.DBDriver;
		DBUSERNAME = Config.DBUserName;
		DBPASSWORD = Config.DBPassword;
		hasData = true;
	}

	public static void testDataBaseConnection() {
		try {
			for (int i = 0; i < 10; i++) {
				Connection conn = getConnection();
				Statement stat = null;
				try {
					stat = conn.createStatement();
					stat.execute("DROP TABLE IF EXISTS `database_test`;");
					stat.execute("CREATE TABLE `database_test` (`test` varchar(50), PRIMARY KEY (`test`));");
					stat.execute("DROP TABLE IF EXISTS `database_test`;");
				} catch (Exception e) {
					LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "数据库连接测试失败");
					LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, e);
				} finally {
					try {
						if (stat != null)
							stat.close();
					} catch (Exception e) {
						LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "数据库连接测试失败");
						LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, e);
					}
				}
				LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "数据库连接成功");
				break;
			}
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
		}
	}
}
