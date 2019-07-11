package com.ybr.intfc;

import com.ybr.entity.MsgHead;
import com.ybr.entity.response.RspBase;

public interface MsgCallBack {

	public void callback(MsgHead head, RspBase rsp);

}
