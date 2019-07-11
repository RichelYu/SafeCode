package com.ybr.entity.response;

import com.ybr.Constants;
import com.ybr.entity.ObjBase;

@SuppressWarnings("serial")
public class RspBase extends ObjBase{
	
	/**
	 * 请求ID(与请求数据一致)
	 */
	public long reqID = 0L;
	/**
	 * 处理状态代码
	 */
	public String rspCode = Constants.RSP_CODE_SUCCESS;

	/**
	 * 处理 结果
	 */
	public String rspMsg = Constants.RSP_MSG_SUCCESS;
}
