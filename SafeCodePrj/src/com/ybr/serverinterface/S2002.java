package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.anno.SvrItfc;
import com.ybr.emnu.CompState;
import com.ybr.entity.request.ReqS2002;
import com.ybr.entity.response.RspS2002;
import com.ybr.exception.MsgException;
import com.ybr.util.CodeUtil;

/**
 * 用户渠道绑定
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "2002", noLoginCmd = true)
public class S2002 extends SvrBase {
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
		stat = conn.createStatement();
		ReqS2002 req = (ReqS2002) this.reqBase;
		RspS2002 rsp = new RspS2002();
		// 检查公司码 和 用户码
		StringBuilder checkUserSql = new StringBuilder();
		checkUserSql.append("select user_id, user_state, user_code_key from t_user where user_key = '")
				.append(req.userKey).append("' ");
		ResultSet rs = stat.executeQuery(checkUserSql.toString());
		if (!rs.next())
			throw new MsgException("用户不存在或用户标识错误");
		if (!rs.getString("user_state").equals("0"))
			throw new MsgException("用户被锁定");
		String userID = rs.getString("user_id");
		String sKey = rs.getString("user_code_key");
		rs.close();

		StringBuilder checkCompSql = new StringBuilder();
		checkCompSql.append("select comp_state from t_comp where comp_key = '").append(req.compKey).append("' ");
		rs = stat.executeQuery(checkCompSql.toString());
		if (!rs.next())
			throw new MsgException("公司标识错误");
		if (!rs.getString("comp_state").equals(CompState.NORMAL))
			throw new MsgException("公司状态异常");
		rs.close();
		
		if (!CodeUtil.equalsSafeCode(userID, req.userKey, sKey, req.safeCode)) 
			throw new MsgException("安全码错误");
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("insert into t_comp_user (comp_key, user_key) values ");
		insertSql.append("('").append(req.compKey).append("', '").append(req.userKey).append("')");
		stat.execute(insertSql.toString());
		this.rspBase = rsp;
	}

	private void checkData() throws MsgException {
		if (!ReqS2002.class.equals(this.reqBase.getClass()))
			throw new MsgException("报文格式错误");
		ReqS2002 req = (ReqS2002) this.reqBase;
		if (req.compKey == null || req.compKey.length() < 1)
			throw new MsgException("公司标识不能为空");
		if (req.userKey == null || req.userKey.length() < 1)
			throw new MsgException("用户标识不能为空");
		if (req.safeCode == null || req.safeCode.length() < 1)
			throw new MsgException("安全码不能为空");

	}

}
