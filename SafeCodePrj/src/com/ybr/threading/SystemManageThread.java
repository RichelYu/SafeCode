package com.ybr.threading;

import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.util.LogUtil;

public class SystemManageThread implements Runnable {

	@Override
	public void run() {
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "服务管理线程启动");
		while (true) {
			try {
				Thread.sleep(5 * 60 * 1000);
				LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "port: " + VarCache.portListenState);
			} catch (Exception e) {
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			}
		}
	}
}
