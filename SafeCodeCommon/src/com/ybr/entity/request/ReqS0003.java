package com.ybr.entity.request;

import com.ybr.anno.Cmd;
import com.ybr.entity.ent.User;

/**
 * 登录
 * @author Hanf_R
 *
 */
@Cmd(cmd = "0003")
public class ReqS0003 extends ReqBase{
	
	public User user = new User();
	
}
