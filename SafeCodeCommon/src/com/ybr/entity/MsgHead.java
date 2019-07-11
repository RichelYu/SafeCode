package com.ybr.entity;

import com.ybr.Constants;
import com.ybr.util.CommonUtil;
import com.ybr.util.LogUtil;

/**
 * 报文头
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class MsgHead extends ObjBase {

	/**
	 * 客户端节点Id length 10 order 1
	 */
	public String rootId = "";

	/**
	 * 终端类型 length 4 order 2
	 */
	public String clientType = "";

	/**
	 * 事务码 length 4 order 3
	 */
	public String dealCode = "";

	/**
	 * 操作员Id length 32 order 4
	 */
	public String userId = "";

	/**
	 * 是否是文件报文 length 1 order 5
	 */
	public String isFile = "";

	/**
	 * 消息类型 广播 普通 心跳 length 1 order 6
	 */
	public String msgType = "";

	@Override
	public final String toString() {
		StringBuffer str = new StringBuffer();
		try {
			str.append(CommonUtil.stringCompletion(rootId, 10));
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			str.append(Constants.MSG_DEFAULT_ROOTID);//0000000000
		}
		try {
			str.append(CommonUtil.stringCompletion(clientType, 4));
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			str.append(Constants.MSG_DEFAULT_CLIENTTYPE);//0000
		}
		try {
			str.append(CommonUtil.stringCompletion(dealCode, 4));
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			str.append(Constants.MSG_DEFAULT_CLIENTTYPE);
		}
		try {
			str.append(CommonUtil.stringCompletion(userId, 32));
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			str.append(Constants.MSG_DEFAULT_USERID);
		}
		try {
			str.append(CommonUtil.stringCompletion(isFile, 1));
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			str.append(Constants.BOOLEAN_FLAG_FALSE);
		}
		try {
			str.append(CommonUtil.stringCompletion(msgType, 1));
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			str.append(Constants.MSG_TYPE_BC);
		}
		return str.toString();
	}

	public static MsgHead praseMsgHead(String value) {
		if (value == null || value.length() != Constants.MSG_HEAD_LENGTH)
			return null;
		MsgHead head = new MsgHead();
		head.rootId = value.substring(0, 10);
		head.clientType = value.substring(10, 14);
		head.dealCode = value.substring(14, 18);
		head.userId = value.substring(18, 50).trim();
		head.isFile = value.substring(50, 51);
		head.msgType = value.substring(51, 52);
		
		return head;
	}

}

