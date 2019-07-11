package com.ybr.entity.ent;

import com.ybr.entity.ObjBase;

/**
 * 用户类
 * @author Hanf_R
 *
 */
public class User extends ObjBase {

	/**
	 * 用户内部标识
	 */
	public String id = "";
	
	/**
	 * 安全码接口安全标识 
	 */
	public String key = "";
	
	/**
	 * 昵称
	 */
	public String name = "";
	
	/**
	 * 密码 加盐md5
	 */
	public String password = "";
	
	/**
	 * 邮箱
	 */
	public String email = "";
}
