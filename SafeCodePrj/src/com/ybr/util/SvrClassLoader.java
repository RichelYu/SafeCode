package com.ybr.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.ybr.Constants;
import com.ybr.Config;
import com.ybr.VarCache;
import com.ybr.anno.SvrItfc;


/**
 * 注解类读取
 * 
 * @author ghf
 *
 */
public class SvrClassLoader {

	private static ArrayList<String> classList = new ArrayList<String>();

	public static void init() {
		try {
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "ClassLoader init 准备进行类装载");
			loadJarClass(Config.JarPath);
			loadClass(Config.ClassPath);
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "ClassLoader init 类加载完成");
			
			HashSet<String> classSet = new HashSet<String>();
			for (String string : classList)
				classSet.add(string);
			for (String className : classSet) {
				Class<?> cla = Thread.currentThread().getContextClassLoader().loadClass(className);
				SvrItfc dealAnno = cla.getAnnotation(SvrItfc.class);
				if (dealAnno != null)
					if (dealAnno.SvrCode() != null && dealAnno.SvrCode().length() > 0){
						VarCache.baseDealMap.put(dealAnno.SvrCode(), className);
						LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "ClassLoader init 注入 事务处理注解类 " + dealAnno.SvrCode() + " -> " + className);
						if (dealAnno.noLoginCmd())
							VarCache.noLoginCmdSet.add(dealAnno.SvrCode());
					}
			}
			classList.clear();
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "ClassLoader init 完成类装载");
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, e);
		}

	}

	/**
	 * 加载未打包的jar的class
	 * 
	 * @param path
	 * @throws Exception
	 */
	private static void loadClass(String path) throws Exception {
		File dir = new File(path);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, "解析class目录:[" + path + "] 不存在");
			throw new Exception("解析class目录:[" + path + "] 不存在");
		}
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "读取路径[" + path + "]的类");
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles();
		ConcurrentLinkedQueue<File> queue = new ConcurrentLinkedQueue<File>();
		for (File file : dirfiles) {
			queue.add(file);
		}
		while (!queue.isEmpty()) {
			File file = queue.poll();
			if (file.isDirectory()) {
				dirfiles = file.listFiles();
				for (File f : dirfiles) {
					queue.add(f);
				}
				continue;
			} else if (file.isFile() && file.getName().endsWith(".class")) {
				String className = file.getPath().substring(path.length()).replaceAll("\\\\", "/");
				className = className.replaceAll("/", ".");
				int index = className.indexOf(Config.PackagePerfix);
				if (index == 0 || index == 1) {
					className = className.substring(index, className.length() - 6);
					classList.add(className);
				}
			}
		}
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "读取路径[" + path + "]完成");
	}

	/**
	 * 分析该目录下jar 里面的Class
	 * 
	 * @param path
	 * @throws Exception
	 */
	private static void loadJarClass(String path) throws Exception {
		File dir = new File(path);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, "解析jar包出错:[" + path + "] 不存在");
			throw new Exception("解析jar包出错:[" + path + "] 不存在");
		}
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "读取路径[" + path + "] 中的 jar");
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles();
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				continue;
			} else if (file.isFile() && file.getName().endsWith(".jar")) {
				JarFile jarFile = new JarFile(file);
				Enumeration<JarEntry> jarEntrys = jarFile.entries();
				while (jarEntrys.hasMoreElements()) {
					JarEntry jarEntry = (JarEntry) jarEntrys.nextElement();
					String filename = jarEntry.getName().replaceAll("/", ".");
					if (filename.endsWith(".class") && filename.startsWith(Config.PackagePerfix)) {
						String className = filename.substring(0, filename.length() - 6);
						classList.add(className);
					}

				}
				jarFile.close();
			}
		}
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "读取路径[" + path + "] 中的 jar 完成");
	}

}

