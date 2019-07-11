package com.ybr;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.ybr.entity.QueueStruct;
import com.ybr.entity.response.RspBase;

public class VarCache {

	public static String rootID = "";

	public static String clientID = "";

	public static String clientType = Constants.CLIENT_TYPE_CLIENT;

	public static LinkedBlockingQueue<QueueStruct> reqQueue = new LinkedBlockingQueue<QueueStruct>();

	public static HashMap<Long, RspBase> rspMap = new HashMap<Long, RspBase>();

	private static long reqID = 1l;

	public synchronized static long getReqID() {
		return reqID++;
	}
}
