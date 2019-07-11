package com.ybr.serverinterface;

import com.ybr.anno.SvrItfc;
import com.ybr.entity.request.ReqS0011;
import com.ybr.entity.response.RspS0011;
import com.ybr.exception.MsgException;

/**
 * 重置密码
 */
@SvrItfc(SvrCode = "0011", noLoginCmd = true)
public class S0011 extends SvrBase {
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
		ReqS0011 req = (ReqS0011) this.reqBase;
		StringBuffer sql = new StringBuffer();
		sql.append("update t_user set user_password = '").append(req.password).append("' where user_email = '").append(req.email).append("' ");
		stat = conn.createStatement();
		stat.executeUpdate(sql.toString());
		this.rspBase = new RspS0011();
	}

	private void checkData() throws Exception {
		if (!ReqS0011.class.equals(this.reqBase.getClass()))
			throw new MsgException("报文格式错误");
		ReqS0011 req = (ReqS0011) this.reqBase;
		if (req.email == null || req.email.trim().length() < 1)
			throw new MsgException("邮箱不能为空");
		if (req.password == null || req.password.trim().length() < 1)
			throw new MsgException("密码不能为空");
	}

}
