package com.ybr;

/**
 * 静态资源类 主要用于存储 配置文件中的配置 属性名 与 配置文件中同名
 * 
 * @author Administrator
 *
 */
public class Config {
	
	/**
	 * 报文编码
	 */
	public static String MSGEncoding;

	/**
	 * 网络 服务器IP 公网或局域网IP 非localhost
	 */
	public static String NETServerIp;

	/**
	 * 网络 监听端口 主通信端口 用于业务处理
	 */
	public static int NETListenPort;

	/**
	 * 网络 广播端口 广播至客户端使用
	 */
	public static int NETBroadcastProt;
	
	/**
	 * 网络 超时时间 单位ms 默认10s
	 */
	public static int NETTimeOut = 10000;
	
	/**
	 * 数据库驱动
	 */
	public static String DBDriver;

	/**
	 * 数据库 数据库路径
	 */
	public static String DBUrl;

	/**
	 * 数据库 数据库用户名
	 */
	public static String DBUserName;

	/**
	 * 数据库 数据库密码
	 */
	public static String DBPassword;
	
	
	/**
	 * 日志输出等级
	 */
	public static int LOGLevel;

	/**
	 * 配置文件刷新时间 单位毫秒 默认10m
	 */
	public static int PropertiesRefushTime = 600000;
	
	/**
	 * 包名前缀 默认 com.hanf
	 */
	public static String PackagePerfix = "com.ybr";
	
	/**
	 * 类路径
	 */
	public static String ClassPath = "./bin";
	
	/**
	 * 架包路径
	 */
	public static String JarPath = "./bin";
	
	/**
	 * 上传下载文件 缓冲区大小  默认值 10kb
	 */
	public static int FileBufferSize = 10240;
	
	/**
	 * 文件仓库路径 默认值 ./file
	 */
	public static String FileFolderPath = "./file";
	
	/**
	 * log4j 配置文件位置
	 */
	public static String LOG4JPropertiesPath = "./log4j.properties";
	
	/**
	 * 最大线程数量
	 */
	public static int MaxThreadNum = 8;
	/**
	 * 邮箱stmp地址 
	 */
	public static String MailServerURL = "stmp.office365.com";
	
	/**
	 * 邮箱服务器端口
	 */
	public static String MailServerPort = "587";
	
	/**
	 * 邮箱地址
	 */
	public static String MailAddress;
	
	/**
	 * 邮箱密码
	 */
	public static String MailPswd;
}
