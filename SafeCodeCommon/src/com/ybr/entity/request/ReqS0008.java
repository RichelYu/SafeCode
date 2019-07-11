package com.ybr.entity.request;

import com.ybr.anno.Cmd;

/**
 * 解绑公司请求
 * @author Hanf_R
 *
 */
@Cmd(cmd="0008")
public class ReqS0008 extends ReqBase{
	
	/**
	 * 公司标识
	 */
	public String compKey = "";
	
}
