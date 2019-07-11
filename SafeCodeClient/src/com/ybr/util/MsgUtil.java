package com.ybr.util;

import com.ybr.Config;
import com.ybr.VarCache;
import com.ybr.entity.QueueStruct;
import com.ybr.entity.request.ReqBase;
import com.ybr.entity.response.RspBase;
import com.ybr.exception.BaseException;
import com.ybr.exception.NetTimeoutException;
import com.ybr.exception.WrongReqClassException;
import com.ybr.intfc.MsgCallBack;

public class MsgUtil {

	public static void sendMsg(ReqBase req, MsgCallBack call) throws BaseException, InterruptedException {
		if (req == null)
			throw new BaseException("req 参数为空");
		if (call == null)
			throw new BaseException("call 参数为空");
		if (ReqBase.class.getName().equals(req.getClass().getName()))
			throw new WrongReqClassException("参数类型必须是ReqBase的派生类");
		VarCache.reqQueue.put(new QueueStruct(req, call));
		
	}
	
	public static RspBase sendMsgWithBlocking(ReqBase req) throws BaseException, InterruptedException{
		if (req == null)
			throw new BaseException("req 参数为空");
		if (ReqBase.class.getName().equals(req.getClass().getName()))
			throw new WrongReqClassException("参数类型必须是ReqBase的派生类");
		req.reqID = VarCache.getReqID();
		long sendTime = System.currentTimeMillis();
		VarCache.reqQueue.put(new QueueStruct(req, null));
		while (true) {
			if (System.currentTimeMillis() - sendTime > Config.NETTimeOut * 3)
				throw new NetTimeoutException("网络超时");
			if (VarCache.rspMap.containsKey(req.reqID)) {
				RspBase rsp = VarCache.rspMap.get(req.reqID);
				VarCache.rspMap.remove(req.reqID);
				return rsp;
			}
		}
	}
}
