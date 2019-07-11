package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.anno.SvrItfc;
import com.ybr.emnu.CompState;
import com.ybr.emnu.UserState;
import com.ybr.entity.request.ReqS1001;
import com.ybr.entity.response.RspS1001;
import com.ybr.exception.MsgException;
import com.ybr.util.CodeUtil;

/**
 * 安全码验证(对外)
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "1001", noLoginCmd = true)
public class S1001 extends SvrBase {
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
		ReqS1001 req = (ReqS1001) this.reqBase;
		RspS1001 rsp = new RspS1001();
		stat = conn.createStatement();
		// 检查公司码 和 用户码
		StringBuilder checkUserSql = new StringBuilder();
		checkUserSql.append("select user_id, user_state, user_code_key from t_user where user_key = '")
				.append(req.userKey).append("' ");
		ResultSet rs = stat.executeQuery(checkUserSql.toString());
		if (!rs.next())
			throw new MsgException("用户不存在或用户标识错误");
		if (!rs.getString("user_state").equals(UserState.NORMAL))
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
		// 检查 公司用户绑定关系
		StringBuilder checkSql = new StringBuilder();
		checkSql.append("select try_count from t_comp_user where comp_key = '").append(req.compKey).append("' ");
		checkSql.append(" and user_key = '").append(req.userKey).append("' ");
		rs = stat.executeQuery(checkSql.toString());
		if (!rs.next())
			throw new MsgException("用户接口无绑定关系");
		int tryCount = rs.getInt("try_count");
		if (tryCount > 9)
			throw new MsgException("尝试次数过多，已锁定用户");
		rs.close();
		// 检查安全码 错误记录次数
		// if (!VarCache.userMap.containsKey(userID))
		// throw new MsgException("用户安全码来源不明");
		if (!CodeUtil.equalsSafeCode(userID, req.userKey, sKey, req.safeCode)) {
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("update t_comp_user set try_count = try_count + 1 ");
			updateSql.append("where comp_key = '").append(req.compKey).append("' ");
			updateSql.append("  and user_key = '").append(req.userKey).append("' ");
			stat.execute(updateSql.toString());
			rsp.rspCode = Constants.RSP_CODE_ERROR;
			rsp.rspMsg = "安全码错误";
			if (tryCount == 9) {
				updateSql = new StringBuilder();
				updateSql.append("update t_user set user_state = '").append(UserState.NORMAL).append("' ");
				updateSql.append("where user_id = '").append(userID).append("'");
				stat.execute(updateSql.toString());
				rsp.rspMsg += ",用户尝试次数达到限制,已被锁定";
			}
		} else {
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("update t_comp_user set try_count = 0 ");
			updateSql.append("where comp_key = '").append(req.compKey).append("' ");
			updateSql.append("  and user_key = '").append(req.userKey).append("' ");
			stat.execute(updateSql.toString());
		}

		this.rspBase = rsp;

	}

	private void checkData() throws Exception {
		if (!this.reqBase.getClass().equals(ReqS1001.class))
			throw new MsgException("报文格式错误");
		ReqS1001 req = (ReqS1001) this.reqBase;
		if (req.compKey == null || req.compKey.length() < 1)
			throw new MsgException("公司标识不能为空");
		if (req.userKey == null || req.userKey.length() < 1)
			throw new MsgException("用户标识不能为空");
		if (req.safeCode == null || req.safeCode.length() < 1)
			throw new MsgException("安全码不能为空");
	}
}
