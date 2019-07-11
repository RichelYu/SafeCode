package com.ybr.serverinterface;

import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.dbio.DBConnection;
import com.ybr.entity.MsgHead;
import com.ybr.entity.request.ReqBase;
import com.ybr.entity.response.RspBase;
import com.ybr.exception.MsgException;
import com.ybr.util.CommonUtil;
import com.ybr.util.LogUtil;

/**
 * 请求或响应处理 基类
 * 
 * @author ghf
 *
 */
public abstract class SvrBase {

	public MsgHead msgHead;
	public ReqBase reqBase;
	public RspBase rspBase;
	/**
	 * socketchannel
	 */
	public SocketChannel sc;

	protected Connection conn;

	protected Statement stat;

	protected PreparedStatement preStat;

	public SvrBase() {
		this.reqBase = new ReqBase();
		this.rspBase = new RspBase();
	}

	public SvrBase(MsgHead msgHead, ReqBase reqBase, SocketChannel sc) {
		this.msgHead = msgHead;
		this.reqBase = reqBase;
		this.sc = sc;
	}

	/**
	 * 取得数据库连接
	 * 
	 * @throws Exception
	 */
	public void openConn() throws Exception {
		this.conn = DBConnection.getConnection();
	}

	/**
	 * 处理事件
	 * 
	 * @throws Exception
	 */
	public void deal() {
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "begin to deal : " + this.msgHead.dealCode);
		try {
			// 进行拦截判断
			if (loginInterceptor()) {
				this.openConn();
				rspBase.rspCode = Constants.RSP_CODE_SUCCESS;
				this.dealDetail();
				conn.commit();
			} else {
				rspBase.rspCode = Constants.RSP_CODE_LOGOUT;
				rspBase.rspMsg = "未登录或登录超时";
			}
		} catch (MsgException e) {
			rspBase.rspCode = Constants.RSP_CODE_ERROR;
			rspBase.rspMsg = CommonUtil.exceptionToString(e);
			LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, e);
			try {
				conn.rollback();
			} catch (Exception temp) {
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, temp);
			}
		} catch (SQLException e) {
			rspBase.rspCode = Constants.RSP_CODE_ERROR;
			rspBase.rspMsg = CommonUtil.exceptionToString(e);
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			try {
				conn.rollback();
			} catch (Exception temp) {
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, temp);
			}
		} catch (Exception e) {
			rspBase.rspCode = Constants.RSP_CODE_ERROR;
			rspBase.rspMsg = CommonUtil.exceptionToString(e);
			LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			try {
				conn.rollback();
			} catch (Exception temp) {
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, temp);
			}
		} finally {
			try {
				if (conn != null)
					this.clear();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
			}
		}
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "deal finish : " + this.msgHead.dealCode);
	}

	/**
	 * 处理事件 需要覆写
	 * 
	 * @throws Exception
	 */
	public abstract void dealDetail() throws Exception;

	/**
	 * 清除变量 需要覆写
	 * 
	 * @throws Exception
	 */
	public abstract void clear() throws Exception;

	/**
	 * 登录拦截
	 * 
	 * @throws MsgException
	 */
	private boolean loginInterceptor() {
		if (VarCache.noLoginCmdSet.contains(this.msgHead.dealCode))
			return true;
		else {
			if (VarCache.loginMap.containsKey(this.msgHead.userId))
				if (VarCache.loginMap.get(this.msgHead.userId).equals(this.msgHead.rootId))
					return true;
		}
		return false;
	}
}
