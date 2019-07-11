package com.ybr.entity.request;

import com.ybr.anno.Cmd;

/**
 * 修改密码
 * @author Hanf_R
 *
 */
@Cmd(cmd = "0010")
public class ReqS0010 extends ReqBase {
	public String uid;
	public String oldPassword;
	public String newPassword;
}
