package com.ybr.entity.request;

import com.ybr.anno.Cmd;

/**
 * 重置密码
 * @author Hanf_R
 *
 */
@Cmd(cmd="0011")
public class ReqS0011 extends ReqBase{
	public String email;
	public String password;
	
}
