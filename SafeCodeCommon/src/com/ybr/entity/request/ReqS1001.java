package com.ybr.entity.request;

import com.ybr.anno.Cmd;

@Cmd(cmd = "1001")
public class ReqS1001 extends ReqBase {
	public String compKey;
	public String userKey;
	public String safeCode;

}
