package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.anno.SvrItfc;
import com.ybr.entity.request.ReqS0005;
import com.ybr.entity.response.RspS0005;
import com.ybr.exception.MsgException;
import com.ybr.util.CodeUtil;

/**
 * 变更用户key
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode="0005")
public class S0005 extends SvrBase{
	@Override
	public void clear() throws Exception {
		if (preStat != null)
			preStat.close();
		if (stat != null)
			stat.close();
	}
	
	
	@Override
	public void dealDetail() throws Exception {
		checkData();
		ReqS0005 req = (ReqS0005)this.reqBase;
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_user where user_id = '").append(req.userId).append("' ");
		stat = conn.createStatement();
		ResultSet rs  = stat.executeQuery(sql.toString());
		if (!rs.next())
			throw new MsgException("用户不存在");
		if (!rs.getString("user_password").equals(req.password))
			throw new MsgException("用户密码错误");
		String oldUKey = rs.getString("user_key");
		String ukey = CodeUtil.getUKey(req.userId);
		String skey = CodeUtil.getSKey();
		sql = new StringBuilder();
		sql.append("update t_user set user_key = '").append(ukey).append("', ");
		sql.append("user_code_key = '").append(skey).append("' ");
		sql.append("where user_id = '").append(req.userId).append("' ");
		stat.execute(sql.toString());
		sql = new StringBuilder();
		sql.append("update t_comp_user set user_key = '").append(ukey).append("' where user_key = '").append(oldUKey).append("' ");
		stat.execute(sql.toString());
		RspS0005 rsp = new RspS0005();
		rsp.userKey = ukey;
		this.rspBase = rsp;
	}
	
	private void checkData() throws Exception{
		if (!ReqS0005.class.equals(this.reqBase.getClass()))
			throw new MsgException("报文格式错误");
		ReqS0005 req = (ReqS0005) this.reqBase;
		if (req.userId == null || req.userId.length() < 1)
			throw new MsgException("用户ID不能为空");
		if (req.password == null || req.password.length() < 1)
			throw new MsgException("用户密码不能为空");
	}

}
