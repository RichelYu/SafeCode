package main.java.com.ybr.util;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import com.ybr.Config;
import com.ybr.Constants;
import com.ybr.anno.Cmd;
import com.ybr.entity.MsgHead;
import com.ybr.entity.request.ReqBase;
import com.ybr.entity.response.RspBase;
import com.ybr.util.CommonUtil;
import com.ybr.util.SocketUtil;



public class MsgUtil {

	public static RspBase sendMsg(ReqBase req) {
		RspBase rsp = new RspBase();
		if (req == null) {
			rsp.rspCode = Constants.RSP_CODE_ERROR;
			rsp.rspMsg = "req 参数为空";
		}
		if (ReqBase.class.getName().equals(req.getClass().getName())) {
			rsp.rspCode = Constants.RSP_CODE_ERROR;
			rsp.rspMsg = "参数类型必须是ReqBase的派生类";
		}
		Cmd cmd_obj = req.getClass().getAnnotation(Cmd.class);
		if (cmd_obj == null) {
			rsp.rspCode = Constants.RSP_CODE_ERROR;
			rsp.rspMsg = req.getClass().getName() + " not has " + Cmd.class.getName() + " anno";
		}
		MsgHead head = new MsgHead();
		head.dealCode = cmd_obj.cmd();
		head.clientType = Constants.CLIENT_TYPE_OPEN;
		head.msgType = Constants.MSG_TYPE_COMM;
		head.isFile = Constants.BOOLEAN_FLAG_FALSE;
		try {
			SocketChannel sc = SocketChannel.open(new InetSocketAddress(Config.NETServerIp, Config.NETListenPort));
			SocketUtil.receiveMsg(sc);
			SocketUtil.sendMsg(sc, head.toString() + req.toString());
			String msg = SocketUtil.receiveMsg(sc);
			// MsgHead recvHead = MsgHead.praseMsgHead(msg.substring(0,
			// Constants.MSG_HEAD_LENGTH));
			rsp = (RspBase) CommonUtil.msgToBeanBase(msg.substring(Constants.MSG_HEAD_LENGTH));
		} catch (Exception e) {
			rsp.rspCode = Constants.RSP_CODE_ERROR;
			rsp.rspMsg = CommonUtil.exceptionToString(e);
		}
		return rsp;
	}
}
