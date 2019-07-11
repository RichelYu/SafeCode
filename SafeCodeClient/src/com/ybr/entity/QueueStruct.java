package com.ybr.entity;

import java.util.Date;

import com.ybr.entity.request.ReqBase;
import com.ybr.exception.BaseException;
import com.ybr.intfc.MsgCallBack;

public class QueueStruct {
	public ReqBase req;
	public MsgCallBack callback;
	/**
	 * 请求创建时间
	 */
	public Date createTime;
	
	public QueueStruct(ReqBase req, MsgCallBack call) throws BaseException{
		this.req = req;
		this.callback = call;
		this.createTime = new Date();
	}
}
