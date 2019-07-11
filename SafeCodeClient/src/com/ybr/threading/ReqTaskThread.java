package com.ybr.threading;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ybr.Config;
import com.ybr.VarCache;
import com.ybr.entity.QueueStruct;

/**
 * 请求任务调度线程
 * @author Hanf_R
 *
 */
public class ReqTaskThread implements Runnable{
	// 创建线程池  灵活调度请求
	private ExecutorService executor = Executors.newCachedThreadPool();
	@Override
	public void run() {
		while(true){
			try{
				QueueStruct req = VarCache.reqQueue.take();
				// 非阻塞请求超时  忽略该请求
				if (req.callback != null && (new Date().getTime() - req.createTime.getTime() > Config.NoBlockingReqTimeOut))
					continue;
				executor.execute(new TransThread(req));
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
