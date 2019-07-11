package com.ybr.threading;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import com.ybr.Config;
import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.anno.Cmd;
import com.ybr.entity.MsgHead;
import com.ybr.entity.QueueStruct;
import com.ybr.entity.request.ReqBase;
import com.ybr.entity.response.RspBase;
import com.ybr.exception.BaseException;
import com.ybr.intfc.MsgCallBack;
import com.ybr.util.CommonUtil;
import com.ybr.util.LogUtil;
import com.ybr.util.SocketUtil;

/**
 * 通讯线程
 * 
 * @author Hanf_R
 *
 */
public class TransThread implements Runnable {

	ReqBase req = null;
	MsgCallBack call = null;

	public TransThread(QueueStruct reqStruct) {
		this.req = reqStruct.req;
		this.call = reqStruct.callback;
	}

	@Override
	public void run() {
		try {
			Cmd cmd_obj = req.getClass().getAnnotation(Cmd.class);
			if (cmd_obj == null)
				throw new BaseException(req.getClass().getName() + " not has " + Cmd.class.getName() + " anno");
			MsgHead head = new MsgHead();
			head.userId = VarCache.clientID;
			head.rootId = VarCache.rootID;
			head.dealCode = cmd_obj.cmd();
			head.clientType = VarCache.clientType;
			head.msgType = Constants.MSG_TYPE_COMM;
			head.isFile = Constants.BOOLEAN_FLAG_FALSE;
			SocketChannel sc = SocketChannel.open(new InetSocketAddress(Config.NETServerIp, Config.NETListenPort));
			SocketUtil.receiveMsg(sc);
			SocketUtil.sendMsg(sc, head.toString() + req.toString());
			String msg = SocketUtil.receiveMsg(sc);
			MsgHead recvHead = MsgHead.praseMsgHead(msg.substring(0, Constants.MSG_HEAD_LENGTH));
			RspBase rsp = (RspBase) CommonUtil.msgToBeanBase(msg.substring(Constants.MSG_HEAD_LENGTH));
			if (call != null)
				call.callback(recvHead, rsp);
			else
				VarCache.rspMap.put(req.reqID, rsp);
			sc.close();
		} catch (Exception e) {
			RspBase rsp = new RspBase();
			rsp.rspCode = Constants.RSP_CODE_ERROR;
			rsp.rspMsg = CommonUtil.exceptionToString(e);
			if (call != null)
				call.callback(new MsgHead(), rsp);
			else
				VarCache.rspMap.put(req.reqID, rsp);
		}
	}
}
