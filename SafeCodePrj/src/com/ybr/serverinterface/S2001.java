package com.ybr.serverinterface;

import com.ybr.anno.SvrItfc;
import com.ybr.entity.request.ReqS2001;
import com.ybr.entity.response.RspS2001;
import com.ybr.exception.MsgException;
import com.ybr.util.CodeUtil;

/**
 * 外系统注册
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "2001", noLoginCmd = true)
public class S2001 extends SvrBase {
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
		ReqS2001 req = (ReqS2001) this.reqBase;
		RspS2001 rsp = new RspS2001();
		String cid = CodeUtil.getCID();
		String ckey = CodeUtil.getCKey(cid);
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_comp (comp_id, comp_key, comp_name, comp_host) values");
		sql.append("('").append(cid).append("', '").append(ckey).append("', '").append(req.compName).append("', '").append(req.compHost).append("' )");
		stat.execute(sql.toString());
		rsp.compID = cid;
		rsp.compKey = ckey;
		rsp.compName = req.compName;
		this.rspBase = rsp;
	}

	private void checkData() throws Exception {
		if (!ReqS2001.class.equals(this.reqBase.getClass()))
			throw new MsgException("报文格式错误");
		ReqS2001 req = (ReqS2001) this.reqBase;
		if (req.compName == null || req.compName.length() < 1)
			throw new MsgException("公司名称不能为空");
		if (req.compHost == null || req.compHost.length() < 1)
			throw new MsgException("公司地址不能为空");
	}
}
