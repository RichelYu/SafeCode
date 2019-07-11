package com.ybr.serverinterface;

import java.sql.ResultSet;

import com.ybr.anno.SvrItfc;
import com.ybr.emnu.VerificationCodeType;
import com.ybr.entity.request.ReqS0100;
import com.ybr.entity.response.RspS0100;
import com.ybr.exception.MsgException;
import com.ybr.util.MailUtil;

/**
 * 获取验证码
 * 
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode="0100", noLoginCmd=true)
public class S0100 extends SvrBase {
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
		ReqS0100 req = (ReqS0100) this.reqBase;
		StringBuffer sql = new StringBuffer();
		sql.append("select count(1) from t_user where user_email = '").append(req.email).append("' ");
		stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql.toString());
		if (!rs.next())
			throw new MsgException("系统错误， 请重试");
		int countNum = rs.getInt(1);
		rs.close();
		if (req.vCodeType.equals(VerificationCodeType.REGIEST)) {
			if (countNum > 0)
				throw new MsgException("邮箱已被注册");
		} else if (req.vCodeType.equals(VerificationCodeType.RESET_PSWD)) {
			if (countNum != 1)
				throw new MsgException("邮箱不存在，无法重置密码");
		} else
			throw new MsgException("验证码类型错误");
		RspS0100 rsp = new RspS0100();
		rsp.vCode = MailUtil.sendValidateMail(req.email);
		this.rspBase = rsp;
	}

	private void checkData() throws Exception {
		if (!ReqS0100.class.equals(this.reqBase.getClass()))
			throw new MsgException("报文格式错误");
		ReqS0100 req = (ReqS0100) this.reqBase;
		if (req.vCodeType == null || req.vCodeType.trim().length() < 1)
			throw new MsgException("验证码类型不能为空");
		if ((!req.vCodeType.equals(VerificationCodeType.REGIEST)) && (!req.vCodeType.equals(VerificationCodeType.RESET_PSWD)))
			throw new MsgException("验证码类型错误");
		if (req.email == null || req.email.trim().length() < 1)
			throw new MsgException("邮箱不能为空");
	}
}
