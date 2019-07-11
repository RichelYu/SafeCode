package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.VarCache;
import com.ybr.anno.SvrItfc;
import com.ybr.emnu.UserState;
import com.ybr.entity.ent.UserSafeCode;
import com.ybr.entity.request.ReqS0003;
import com.ybr.entity.response.RspS0003;
import com.ybr.exception.MsgException;
import com.ybr.util.CommonUtil;

/**
 * 登录
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "0003", noLoginCmd = true)
public class S0003 extends SvrBase {
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
		this.stat = this.conn.createStatement();
		ReqS0003 req = (ReqS0003) this.reqBase;
		RspS0003 rsp = new RspS0003();
		StringBuilder sql = new StringBuilder();
		sql.append("select * from t_user where user_password = '").append(req.user.password).append("' ");
		sql.append("and user_email = '").append(req.user.email).append("' ");
		ResultSet rs = stat.executeQuery(sql.toString());
		UserSafeCode usc = new UserSafeCode();
		if (rs.next()) {
			String user_state = rs.getString("user_state");
			if (!user_state.equals(UserState.NORMAL))
				if (user_state.equals(UserState.FREEZE))
					throw new MsgException("用户被冻结");
				else if (user_state.equals(UserState.DELETE))
					throw new MsgException("用户已注销");
				else
					throw new MsgException("未知用户状态");
			req.user.id = rs.getString("user_id");
			req.user.key = rs.getString("user_key");
			req.user.name = rs.getString("user_name");
			req.user.email = rs.getString("user_email");
			req.user.password = "";
			usc.uid = req.user.id;
			usc.ukey = req.user.key;
			usc.skey = rs.getString("user_code_key");
		} else
			throw new MsgException("用户名或密码错误");
		rsp.user = req.user;
		String rootID = CommonUtil.randomRootId();
		while (VarCache.loginMap.containsValue(rootID))
			rootID = CommonUtil.randomRootId();
		rsp.rootID = rootID;
		// 缓存注册
		VarCache.loginMap.put(rsp.user.id, rootID);
		VarCache.userMap.put(req.user.id, usc);
		this.rspBase = rsp;
	}

	private void checkData() throws MsgException {
		if (this.reqBase.getClass() != ReqS0003.class)
			throw new MsgException("请求数据错误");
		ReqS0003 req = (ReqS0003) this.reqBase;
		if (req.user == null)
			throw new MsgException("用户数据缺失");
		if (req.user.email == null || req.user.email.trim().length() < 1)
			throw new MsgException("用户手机或邮箱为空");
		if (req.user.password == null || req.user.password.trim().length() < 1)
			throw new MsgException("用户密码为空");
	}

}
