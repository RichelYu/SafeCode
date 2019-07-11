package com.ybr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import com.ybr.Constants;
import com.ybr.Config;

/**
 * 数据初始化
 * 
 * @author Administrator
 *
 */
public class PropertiesLoader {

	/**
	 * Config.properties 初始化 -> Properties.class
	 */
	public static void initPropertiesData() {
		try {
			File folder = new File(Constants.configPropertiesFilePath);
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "begin search config from : " + folder.getPath());
			File f = searchPropertiesFile(folder);
			if(f == null)
				throw new Exception("配置文件不存在, 无法启动系统");
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "find config at : " + f.getPath());
			FileInputStream fin = new FileInputStream(f);
			Properties p = new Properties();
			p.load(fin);
			Field[] fs = Config.class.getFields();
			for (Field field : fs) {
				String str = p.getProperty(field.getName());
				if (str != null && str.length() > 0)
					field.set(null, CommonUtil.stringToOwnType(field, str));
			}
			p.clear();
			fin.close();
			LogUtil.setLogLevel(Config.LOGLevel);
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "config load finish");
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, e);
		}
	}
	
	/**
	 * 搜索配置文件
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static File searchPropertiesFile(File f) throws IOException{
		if(!f.exists())
			return null;
		File temp = null;
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for (File file : files){
				File t = null;
				if((t = searchPropertiesFile(file)) != null)
					temp = t;
			}
			return temp;
		}else if(f.isFile()){
			if(f.getName().equals(Constants.configPropertiesFileName))
				temp = f;
		}
		return temp;
	}
}
