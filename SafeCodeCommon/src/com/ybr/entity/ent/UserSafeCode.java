package com.ybr.entity.ent;

import com.ybr.entity.ObjBase;

public class UserSafeCode extends ObjBase{
	public String uid;
	//用户对外ID
	public String ukey;
	//安全码偏移度
	public String skey;
	public String scode;
	public long validityTime = -1;
}
