package com.ybr.serverinterface;

import java.util.Date;

import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.anno.SvrItfc;
import com.ybr.entity.ent.UserSafeCode;
import com.ybr.entity.response.RspS0004;
import com.ybr.exception.MsgException;
import com.ybr.util.CodeUtil;
import com.ybr.util.LogUtil;

/**
 * 获取安全码
 * @author Hanf_R
 *
 */
@SvrItfc(SvrCode = "0004")
public class S0004 extends SvrBase {
	public void clear() throws Exception {
		if (stat != null)
			stat.close();
		if (preStat != null)
			preStat.close();
	}

	@Override
	public void dealDetail() throws Exception {
		if (!VarCache.userMap.containsKey(this.msgHead.userId))
			throw new MsgException("登录超时,重新登录后再试");
		UserSafeCode usc = VarCache.userMap.get(this.msgHead.userId);
		if (usc == null)
			throw new MsgException("登录信息缺失，重新登录");
		
		RspS0004 rsp = new RspS0004();
		if (usc.validityTime < 1 || usc.scode == null || new Date().getTime() >= usc.validityTime) {
			usc.scode = CodeUtil.getNowSafeCode(usc.uid, usc.ukey, usc.skey);
			usc.validityTime = CodeUtil.getValidityTime();
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO,
					String.format("recalcul safe code: %s, %s", usc.scode, new Date(usc.validityTime)));
		}
		rsp.safeCode = usc.scode;
		rsp.validityTime = new Date(usc.validityTime);
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO,
				String.format("cache safe code: %s, %s", usc.scode, new Date(usc.validityTime)));
		this.rspBase = rsp;
	}

}
