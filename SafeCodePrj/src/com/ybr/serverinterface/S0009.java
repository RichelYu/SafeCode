package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.anno.SvrItfc;
import com.ybr.entity.ent.Comp;
import com.ybr.entity.request.ReqS0009;
import com.ybr.entity.response.RspS0009;
import com.ybr.exception.MsgException;
import com.ybr.struct.ArrayList;

/**
 * 查询绑定信息
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "0009")
public class S0009 extends SvrBase {
	ReqS0009 req = null;
	RspS0009 rsp = null;

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
		req = (ReqS0009) this.reqBase;
		rsp = new RspS0009();
		rsp.comps = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select comp_key, comp_name, comp_host from t_comp ");
		sql.append("where comp_key in (select comp_key from t_comp_user where user_key in (");
		sql.append("select user_key from t_user where user_id = '").append(msgHead.userId).append("')) ");
		stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql.toString());
		while (rs.next()) {
			Comp c = new Comp();
			c.compKey = rs.getString("comp_key");
			c.compName = rs.getString("comp_name");
			c.compHost = rs.getString("comp_host");
			rsp.comps.add(c);
		}
		this.rspBase = rsp;
	}

	private void checkData() throws Exception {
		if (this.reqBase == null)
			throw new MsgException("请求数据为空");
		if (!this.reqBase.getClass().equals(ReqS0009.class))
			throw new MsgException("请求数据格式错误");
		ReqS0009 reqTemp = (ReqS0009) this.reqBase;
		if (this.msgHead.userId.trim().length() < 1)
			throw new MsgException("报文头中必须包含登录用户的用户ID");
	}

}
