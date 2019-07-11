package com.ybr.entity.request;

import com.ybr.anno.Cmd;

/**
 * 获取验证码
 * @author Hanf_R
 *
 */
@Cmd(cmd = "0100")
public class ReqS0100 extends ReqBase{
	
	/**
	 * 验证码类型  1:注册  2:重置密码
	 */
	public String vCodeType;
	
	public String email;
}
