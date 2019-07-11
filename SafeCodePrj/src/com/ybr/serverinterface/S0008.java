package com.ybr.serverinterface;

import com.ybr.anno.SvrItfc;
import com.ybr.entity.request.ReqS0008;
import com.ybr.entity.response.RspS0008;
import com.ybr.exception.MsgException;

/**
 * 解绑公司
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "0008")
public class S0008 extends SvrBase {
	ReqS0008 req = null;
	RspS0008 rsp = null;

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
		req = (ReqS0008) this.reqBase;
		rsp = new RspS0008();
		StringBuffer sql = new StringBuffer();
		sql.append("delete from t_comp_user where user_key in (");
		sql.append("select user_key from t_user where user_id = '");
		sql.append(this.msgHead.userId).append("') and comp_key = '");
		sql.append(req.compKey).append("' ");
		stat = this.conn.createStatement();
		stat.execute(sql.toString());
		this.rspBase = rsp;
	}

	private void checkData() throws Exception {
		if (this.reqBase == null)
			throw new MsgException("请求数据为空");
		if (!this.reqBase.getClass().equals(ReqS0008.class))
			throw new MsgException("请求数据格式错误");
		ReqS0008 reqTemp = (ReqS0008)this.reqBase;
		if (this.msgHead.userId.trim().length() < 1)
			throw new MsgException("报文头中必须包含登录用户的用户ID");
		if (reqTemp.compKey == null || reqTemp.compKey.trim().length() < 1)
			throw new MsgException("公司标识不能为空");
	}

}
