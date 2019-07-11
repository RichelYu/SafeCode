package com.ybr.serverinterface;

import com.ybr.VarCache;
import com.ybr.anno.SvrItfc;
import com.ybr.entity.response.RspS0007;
import com.ybr.exception.MsgException;

/**
 * 注销
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "0007")
public class S0007 extends SvrBase {

	@Override
	public void clear() throws Exception {
		if (stat != null)
			stat.close();
		if (preStat != null)
			preStat.close();
	}

	@Override
	public void dealDetail() throws Exception {
		checkData();
//		ReqS0007 req = (ReqS0007)this.reqBase;
		VarCache.loginMap.remove(this.msgHead.userId.trim());
		VarCache.userMap.remove(this.msgHead.userId.trim());
		this.rspBase = new RspS0007();
	}

	private void checkData() throws Exception {
		if (this.msgHead.userId.trim().length() < 1)
			throw new MsgException("报文头中必须包含登录用户的用户ID");
	}
}
