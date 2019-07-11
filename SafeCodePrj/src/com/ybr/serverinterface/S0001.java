package com.ybr.serverinterface;

import com.ybr.anno.SvrItfc;
import com.ybr.entity.request.ReqS0001;
import com.ybr.entity.response.RspS0001;

/**
 * 服务器时间同步
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode="0001", noLoginCmd = true)
public class S0001 extends SvrBase{
	@Override
	public void clear() throws Exception {
		if (this.stat != null)
			this.stat.close();
		if (this.preStat != null)
			this.preStat.close();
	}
	@Override
	public void dealDetail() throws Exception {
		ReqS0001 req = (ReqS0001) this.reqBase;
		RspS0001 rsp = new RspS0001();
		this.rspBase = rsp;
	}
}
