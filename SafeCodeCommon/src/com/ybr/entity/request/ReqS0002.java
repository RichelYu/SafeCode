package com.ybr.entity.request;

import com.ybr.anno.Cmd;
import com.ybr.entity.ent.User;

/**
 * 注册
 * @author Hanf_R
 *
 */
@Cmd(cmd = "0002")
public class ReqS0002 extends ReqBase{
	
	public User user;
	
}
