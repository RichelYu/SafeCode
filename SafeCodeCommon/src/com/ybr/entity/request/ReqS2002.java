package com.ybr.entity.request;

import com.ybr.anno.Cmd;

@Cmd(cmd = "2002")
public class ReqS2002 extends ReqBase {

	/**
	 * 渠道标识
	 */
	public String compKey;

	/**
	 * 用户标识
	 */
	public String userKey;

	/**
	 * 安全码
	 */
	public String safeCode;

}
