package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.anno.SvrItfc;
import com.ybr.entity.request.ReqS0010;
import com.ybr.entity.response.RspS0010;
import com.ybr.exception.MsgException;

/**
 * 修改密码
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "0010")
public class S0010 extends SvrBase {
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
		ReqS0010 req = (ReqS0010) this.reqBase;
		StringBuffer sql = new StringBuffer();
		sql.append("select user_password from t_user where user_id = '").append(req.uid).append("' ");
		stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql.toString());
		if (! rs.next())
			throw new MsgException("用户不存在");
		String dbPassword = rs.getString("user_password");
		if (!dbPassword.equals(req.oldPassword))
			throw new MsgException("当前密码错误，不能修改密码");
		sql = new StringBuffer();
		sql.append("update t_user set user_password = '").append(req.newPassword).append("' where user_id = '").append(req.uid).append("' ");
		stat.execute(sql.toString());
		this.rspBase = new RspS0010();
	}

	private void checkData() throws Exception {
		if (!this.reqBase.getClass().equals(ReqS0010.class))
			throw new MsgException("报文格式错误");
		ReqS0010 req = (ReqS0010) this.reqBase;
		if (req.uid == null || req.uid.trim().length() < 1)
			throw new MsgException("用户标识不能为空");
		if (req.oldPassword == null || req.oldPassword.trim().length() < 1)
			throw new MsgException("当前密码不能为空");
		if (req.newPassword == null || req.newPassword.trim().length() < 1)
			throw new MsgException("新密码不能为空");
	}

}
