package com.ybr;

public class Constants {

	
	
	
	public static final String projectName = "SafeCode";
	
	/**
	 * 安全码有效期
	 */
	public static final long safeCodeValidityDate = 15000;
	
	/**
	 * Config.properties 配置文件路径 value = "./src/Config.properties"
	 */
	public static final String configPropertiesFilePath = "./";
	
	/**
	 * 配置文件名
	 */
	public static final String configPropertiesFileName = "svrcfg.properties";
	
	/**
	 * 日志等级 致命 value = 1
	 */
	public static final int LOG_LEVEL_FATAL = 1;
	/**
	 * 日志等级 错误 value = 2
	 */
	public static final int LOG_LEVEL_ERROR = 2;
	/**
	 * 日志等级 警告 value = 3
	 */
	public static final int LOG_LEVEL_WARNING = 3;
	/**
	 * 日志等级 信息 value = 4
	 */
	public static final int LOG_LEVEL_INFO = 4;
	/**
	 * 日志等级 调试 value = 5
	 */
	public static final int LOG_LEVEL_DEBUG = 5;

	/**
	 * 终端类型 员工端 value = "1001"
	 */
	public static final String CLIENT_TYPE_CLIENT = "1001";

	/**
	 * 终端类型 管理端 value = "1002"
	 */
	public static final String CLIENT_TYPE_MANAGER = "1002";

	/**
	 * 终端类型 备用 value = "1003"
	 */
	public static final String CLIENT_TYPE_OPEN = "1003";

	/**
	 * boolean 类型标志 真 value = "1"
	 */
	public static final String BOOLEAN_FLAG_TURE = "1";

	/**
	 * boolean 类型标志 假 value = "0"
	 */
	public static final String BOOLEAN_FLAG_FALSE = "0";

	/**
	 * 报文类型 普通 value = "0"
	 */
	public static final String MSG_TYPE_COMM = "0";

	/**
	 * 报文类型 心跳 value = "1"
	 */
	public static final String MSG_TYPE_HEARTBEAT = "1";

	/**
	 * 报文类型 广播 value = "2"
	 */
	public static final String MSG_TYPE_BC = "2";

	/**
	 * 报文头长度
	 */
	public static final int MSG_HEAD_LENGTH = 10 + 4 + 4 + 32 + 1 + 1;

	/**
	 * 字段补长默认字符  value = ' '
	 */
	public static final char MSG_DEFAULT_COMPLETIONCHAR = ' ';
	
	/**
	 * 默认节点值
	 */
	public static final String MSG_DEFAULT_ROOTID = "0000000000";
	
	/**
	 * 默认终端类型
	 */
	public static final String MSG_DEFAULT_CLIENTTYPE = "0000";
	
	/**
	 * 默认事务码
	 */
	public static final String MSG_DEFAULT_DEALCODE = "XXXX";
	
	/**
	 * 默认用户ID
	 */
	public static final String MSG_DEFAULT_USERID = "00000000000000000000000000000000";
	
	/**
	 * 处理结果状态码 成功 value = "000000"
	 */
	public static final String RSP_CODE_SUCCESS = "000000";

	/**
	 * 处理结果状态码 出错 value = "111111"
	 */
	public static final String RSP_CODE_ERROR = "111111";

	/**
	 * 处理结果状态码 签退 value = "1f2x14"
	 */
	public static final String RSP_CODE_LOGOUT = "1f2x14";
	/**
	 * 处理结果信息 成功 默认信息 value = "处理成功"
	 */
	public static final String RSP_MSG_SUCCESS = "处理成功";

	/**
	 * BeanBase toString 左括号
	 * value = '('
	 */
	public static final char CHAR_BEANBASE_BRACKET_LEFT = '(';
	
	/**
	 * BeanBase toStirng 右括号
	 * value = ')'
	 */
	public static final char CHAR_BEANBASE_BRACKET_RIGHT = ')';
	
	/**
	 * HashMap toString 左括号
	 * value = '{'
	 */
	public static final char CHAR_HASHMAP_BRACKET_LEFT = '{';
	/**
	 * HashMap tString 右括号
	 * value = '}'
	 */
	public static final char CHAR_HASHMAP_BRACKET_RIGHT = '}';
	/**
	 * HashMap toString 键值分割符
	 * value = '>'
	 */
	public static final char CHAR_HASHMAP_SEPARATE_KEYVALUE = '>';
	/**
	 * HashMap toString 组分割符
	 * value = ';'
	 */
	public static final char CHAR_HASHMAP_SEPARATE_GROUP = ';';
	
	/**
	 * ArrayList toString 左括号
	 * value = '['
	 */
	public static final char CHAR_ARRAYLIST_BRACKET_LEFT = '[';
	/**
	 * ArrayList toString 右括号
	 * value = ']'
	 */
	public static final char CHAR_ARRAYLIST_BRACKET_RIGHT = ']';
	/**
	 * ArrayList toString 元素分割符
	 * value = ','
	 */
	public static final char CHAR_ARRAYLIST_SEPARATE = ',';
	
	/**
	 * 报文 toString 元素分割符
	 * value = '#'
	 */
	public static final char CHAR_MSG_SEPARATE = '#';
	/**
	 * 报文 头String 键值分隔符
	 * value = '='
	 */
	public static final char CHAR_MSG_EQUAL = '=';
	
	/**
	 * String 转义头 
	 * value = '∧'
	 */
	public static final char CHAR_STRING_MSG_HEAD = '~';
	
	/**
	 * 网络通讯报文编码
	 */
	public static final String MSG_ENCODING = "UTF-8";
}
