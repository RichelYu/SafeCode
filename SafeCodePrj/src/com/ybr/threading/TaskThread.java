package com.ybr.threading;

import java.nio.channels.SocketChannel;

import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.entity.MsgHead;
import com.ybr.entity.ObjBase;
import com.ybr.entity.request.ReqBase;
import com.ybr.entity.response.RspBase;
import com.ybr.serverinterface.SvrBase;
import com.ybr.util.CommonUtil;
import com.ybr.util.LogUtil;
import com.ybr.util.SocketUtil;

public class TaskThread implements Runnable {

	private SocketChannel sc = null;

	public TaskThread(SocketChannel sc) {
		this.sc = sc;
	}

	@Override
	public void run() {
		try {
			SocketUtil.sendMsg(sc, "OK");

			String msg = SocketUtil.receiveMsg(sc);

			MsgHead msgHead = MsgHead.praseMsgHead(msg.substring(0, Constants.MSG_HEAD_LENGTH));

			ObjBase bean = CommonUtil.msgToBeanBase(msg.substring(Constants.MSG_HEAD_LENGTH));

			switch (msgHead.msgType) {
			// 普通报文处理
			case Constants.MSG_TYPE_COMM:
				Class<?> dealBean = Class.forName(VarCache.baseDealMap.get(msgHead.dealCode));
				SvrBase dealBase = (SvrBase) dealBean.newInstance();
				dealBase.msgHead = msgHead;
				dealBase.reqBase = (ReqBase) bean;
				dealBase.sc = sc;
				dealBase.deal();
				dealBase.rspBase.reqID = dealBase.reqBase.reqID;
				RspBase rspBase = dealBase.rspBase;
				SocketUtil.sendMsg(sc, msgHead.toString() + rspBase.toString());
				break;
			// 心跳包处理
			case Constants.MSG_TYPE_HEARTBEAT:
				break;
			}
		} catch (Exception e) {
			try {
				if (sc != null) {
					RspBase rsp = new RspBase();
					rsp.rspCode = Constants.RSP_CODE_ERROR;
					rsp.rspMsg = CommonUtil.exceptionToString(e);
					SocketUtil.sendMsg(sc, new MsgHead().toString() + rsp.toString());
				}
			} catch (Exception ee) {
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, ee);
			}
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
		} finally {
			try {
				if (sc != null) {
					sc.close();
				}
			} catch (Exception ee) {
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, ee);
			}
		}
	}

}
