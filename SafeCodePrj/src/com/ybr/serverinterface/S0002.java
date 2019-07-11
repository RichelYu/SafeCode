package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.anno.SvrItfc;
import com.ybr.emnu.UserState;
import com.ybr.entity.ent.User;
import com.ybr.entity.request.ReqS0002;
import com.ybr.entity.response.RspS0002;
import com.ybr.exception.MsgException;
import com.ybr.util.CodeUtil;

/**
 * 注册
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "0002", noLoginCmd = true)
public class S0002 extends SvrBase {
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
		ReqS0002 req = (ReqS0002) this.reqBase;
		RspS0002 rsp = new RspS0002();
		stat = conn.createStatement();
		StringBuilder sql = new StringBuilder();
		User user = req.user;
		sql.append("select 1 from t_user where user_email = '").append(user.email).append("' ");
		System.out.println(sql.toString());
		ResultSet rs = stat.executeQuery(sql.toString());
		if (rs.next())
			throw new MsgException("邮箱已绑定");
		rs.close();
		user.id = CodeUtil.getUID();
		user.key = CodeUtil.getUKey(user.id);
		String code_key = CodeUtil.getSKey();
		sql = new StringBuilder();
		sql.append(
				"insert into t_user(user_id, user_name, user_key, user_email, user_password, user_code_key, user_state)");
		sql.append("values('").append(user.id).append("', ");
		sql.append("'").append(user.name).append("', ");
		sql.append("'").append(user.key).append("', ");
		sql.append("'").append(user.email).append("', ");
		sql.append("'").append(user.password).append("', ");
		sql.append("'").append(code_key).append("', ");
		sql.append("'").append(UserState.NORMAL).append("')");
		stat.execute(sql.toString());
		sql = new StringBuilder();
		sql.append("select * from t_user where user_id = '").append(user.id).append("'");
		rs = stat.executeQuery(sql.toString());
		if (rs.next()) {
			rsp.user = new User();
			rsp.user.id = rs.getString("user_id");
			rsp.user.id = rs.getString("user_name");
			rsp.user.key = rs.getString("user_key");
			rsp.user.email = rs.getString("user_email");
		} else
			throw new MsgException("注册失败");
		rs.close();
		this.rspBase = rsp;
	}

	public void checkData() throws MsgException {
		if (this.reqBase.getClass() != ReqS0002.class)
			throw new MsgException("错误的请求数据");
		ReqS0002 req = (ReqS0002) this.reqBase;
		if (req.user.name == null || req.user.name.trim().length() < 1)
			throw new MsgException("昵称为空");
		if (req.user.password == null || req.user.password.trim().length() < 1)
			throw new MsgException("密码为空");
		if (req.user.email == null || req.user.email.trim().length() < 1)
			throw new MsgException("邮箱为空");
	}

}
