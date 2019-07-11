package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.anno.SvrItfc;
import com.ybr.emnu.UserState;
import com.ybr.entity.request.ReqS0006;
import com.ybr.entity.response.RspS0006;
import com.ybr.exception.MsgException;

/**
 * 解冻
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "0006", noLoginCmd = true)
public class S0006 extends SvrBase {

	public void clear() throws Exception {
		if (stat != null)
			stat.close();
		if (preStat != null)
			preStat.close();
	}

	public void dealDetail() throws Exception {
		checkData();
		ReqS0006 req = (ReqS0006) this.reqBase;
		RspS0006 rsp = new RspS0006();
		stat = conn.createStatement();
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_user where user_email = '").append(req.email).append("' ");
		ResultSet rs = stat.executeQuery(sql.toString());
		if (!rs.next())
			throw new MsgException("用户不存在");
		String uID = rs.getString("user_id");
		String uKey = rs.getString("user_key");
		String pswd = rs.getString("user_password");
		if (!req.password.equals(pswd))
			throw new MsgException("密码错误");
		sql = new StringBuilder();
		sql.append("update t_user set user_state = '").append(UserState.NORMAL).append("' where user_id = '").append(uID).append("' ");
		stat.execute(sql.toString());
		sql = new StringBuilder();
		sql.append("update t_comp_user set try_count = 0 where user_key = '").append(uKey).append("' and try_count > 0");
		stat.execute(sql.toString());
		this.rspBase = rsp;
	}

	private void checkData() throws Exception {
		if (!ReqS0006.class.equals(this.reqBase.getClass()))
			throw new MsgException("报文格式错误");
		ReqS0006 req = (ReqS0006) this.reqBase;
		if (req.email == null || req.email.length() < 1)
			throw new MsgException("邮箱不能为空");
		if (req.password == null || req.password.length() < 1)
			throw new MsgException("密码不能为空");
	}
}
