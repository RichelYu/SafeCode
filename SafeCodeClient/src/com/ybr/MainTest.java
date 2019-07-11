package com.ybr;

import com.ybr.entity.MsgHead;
import com.ybr.entity.request.ReqS0003;
import com.ybr.entity.request.ReqS0004;
import com.ybr.entity.request.ReqS0100;
import com.ybr.entity.request.ReqS2001;
import com.ybr.entity.request.ReqS2002;
import com.ybr.entity.response.RspBase;
import com.ybr.entity.response.RspS0003;
import com.ybr.entity.response.RspS0004;
import com.ybr.entity.response.RspS2001;
import com.ybr.exception.BaseException;
import com.ybr.intfc.MsgCallBack;
import com.ybr.threading.ReqTaskThread;
import com.ybr.util.MsgUtil;

public class MainTest {

	public static void main(String[] args) {
		new Thread(new ReqTaskThread()).start();
//		getSafeCode();
		getSafeCode();
	}

	public static void regComp() {
		ReqS2001 req = new ReqS2001();
		req.compName = "第一渠道";
		req.compHost = "123";
		try {
			MsgUtil.sendMsg(req, new MsgCallBack() {
				@Override
				public void callback(MsgHead head, RspBase rsp) {
					if (rsp.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
						RspS2001 rsp2001 = (RspS2001) rsp;
						System.out
								.println(String.format("reg succ cid: %s, ckey: %s", rsp2001.compID, rsp2001.compKey));
						ReqS0003 req = new ReqS0003();
						req.user.email = "18838958301@qq.com";
						req.user.password = "123123";
						try {
							MsgUtil.sendMsg(req, new MsgCallBack() {
								@Override
								public void callback(MsgHead head, RspBase rsp) {
									if (rsp.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
										RspS0003 rsp0003 = (RspS0003) rsp;
										System.out.println(rsp.rspMsg);
										VarCache.rootID = rsp0003.rootID;
										VarCache.clientID = rsp0003.user.id;
										ReqS0004 req = new ReqS0004();
										try {
											MsgUtil.sendMsg(req, new MsgCallBack() {
												@Override
												public void callback(MsgHead head, RspBase rsp) {
													if (rsp.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
														RspS0004 rsp0004 = (RspS0004) rsp;
														ReqS2002 req = new ReqS2002();
														req.compKey = rsp2001.compKey;
														req.userKey = rsp0003.user.key;
														req.safeCode = rsp0004.safeCode;
														try {
															MsgUtil.sendMsg(req, new MsgCallBack() {
																@Override
																public void callback(MsgHead head, RspBase rsp) {
																	if (rsp.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
																		System.out.println("bind succ");
																	}else{
																		System.out.println("bind fail");
																	}
																	System.exit(0);
																}
															});
														} catch (BaseException e) {
															e.printStackTrace();
														} catch (InterruptedException e) {
															e.printStackTrace();
														}
													} else
														System.out.println(rsp.rspMsg);
												}
											});
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										System.out.println(rsp.rspMsg);
									}
								}
							});
						} catch (BaseException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println(rsp);
					}
				}
			});
		} catch (BaseException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void getSafeCode() {
		try {
			ReqS0003 req = new ReqS0003();
			req.user.email = "435383440@qq.com";
			req.user.password = "1738";

			MsgUtil.sendMsg(req, new MsgCallBack() {

				@Override
				public void callback(MsgHead head, RspBase rsp) {
					if (rsp.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
						RspS0003 rsp0003 = (RspS0003) rsp;
						System.out.println(rsp.rspMsg);
						VarCache.rootID = rsp0003.rootID;
						VarCache.clientID = rsp0003.user.id;
						ReqS0004 req = new ReqS0004();
						try {
							while (true) {
								MsgUtil.sendMsg(req, new MsgCallBack() {
									@Override
									public void callback(MsgHead head, RspBase rsp) {
										if (rsp.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
											RspS0004 rsp0004 = (RspS0004) rsp;
											System.out.println("safe code:" + rsp0004.safeCode + ", vt:"
													+ rsp0004.validityTime);
										} else
											System.out.println(rsp.rspMsg);
									}
								});
								Thread.sleep(1000);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						System.out.println(rsp.rspMsg);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getVCode(){
		ReqS0100 req = new ReqS0100();
		req.vCodeType = "1";
		req.email = "376840834@qq.com";
		try {
			MsgUtil.sendMsg(req, new MsgCallBack() {
				@Override
				public void callback(MsgHead head, RspBase rsp) {
					System.out.println(rsp);
				}
			});
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}