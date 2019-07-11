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
	public static String MSGEncoding = "utf-8";

	/**
	 * 网络 服务器IP 公网或局域网IP 非localhost
	 */
	public static String NETServerIp = "101.132.33.200";

	/**
	 * 网络 监听端口 主通信端口 用于业务处理
	 */
	public static int NETListenPort = 26927;

	
	/**
	 * 网络 超时时间 单位ms 默认10s
	 */
	public static int NETTimeOut = 10000;
	
	/**
	 * 日志输出等级
	 */
	public static int LOGLevel;

	/**
	 * 非阻塞请求超时时间
	 */
	public static int NoBlockingReqTimeOut = 60000;
	
	/**
	 * 上传下载文件 缓冲区大小  默认值 10kb
	 */
	public static int FileBufferSize = 10240;
	
}
