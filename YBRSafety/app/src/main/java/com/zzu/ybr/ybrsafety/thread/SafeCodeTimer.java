package com.zzu.ybr.ybrsafety.thread;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ybr.Constants;
import com.ybr.entity.MsgHead;
import com.ybr.entity.request.ReqS0001;
import com.ybr.entity.request.ReqS0004;
import com.ybr.entity.response.RspBase;
import com.ybr.entity.response.RspS0001;
import com.ybr.entity.response.RspS0004;
import com.ybr.exception.BaseException;
import com.ybr.util.MsgUtil;
import com.zzu.ybr.ybrsafety.R;

import java.util.Date;

public class SafeCodeTimer implements Runnable{
    private Date validityDate = null; // 当前安全码有效时间
    private long totalTime = 15000;//总时间 15s  单位ms
    private Long diffTime = null;// 服务器 - 客户端 ms
    private Handler handler = null;
    private boolean canRun = true;
    private ReqS0004 reqS0004 = new ReqS0004();
    private ReqS0001 reqS0001 = new ReqS0001();
    public SafeCodeTimer(Handler handler){
        this.handler = handler;
    }

    public void stopTimer(){
        this.canRun = false;
    }

    private Message setProgress(int progress){
        Bundle bdl = new Bundle();
        bdl.putInt("progress", progress);
        Message msg = new Message();
        msg.setData(bdl);
        return msg;
    }
    public void run(){

        // 同步服务器时间
        try {
            // 阻塞请求同步服务器时间
            long beginTime = System.currentTimeMillis();
            RspBase rsp = MsgUtil.sendMsgWithBlocking(reqS0001);
            if (Constants.RSP_CODE_SUCCESS.equals(rsp.rspCode)){
                long endTime = System.currentTimeMillis();
                long delayTime = endTime - beginTime == 0 ? 0 : (endTime - beginTime) / 2; // 避免除0
                RspS0001 rsp0001 = (RspS0001)rsp;
                diffTime = rsp0001.svrDate.getTime() - new Date().getTime() + delayTime; // 时间同步时 应该把网络通讯的时间计算进去
            }else {
                // TODO 失败的异常处理  建议向handler发送失败的通知
                return;// TODO 然后return 退出线程
            }
        }catch (Exception e){  // TODO 事实上所有的异常都应该以合理的方式告知前端，然后让用户知晓  程序也要做出处理 退出应用之类的
            e.printStackTrace();
        }
        // 定时任务 10ms
        while(this.canRun){
            try{
                Thread.sleep(10);
                // 等待时间同步
                if (diffTime == null) {
                    handler.sendMessage(setProgress(0));
                    continue;
                }
                long nowSvrTime = new Date().getTime() + diffTime.longValue();
                if (validityDate != null && nowSvrTime < validityDate.getTime()){
                    handler.sendMessage(setProgress((int)(totalTime - validityDate.getTime() + nowSvrTime)));
                }else {

                    RspBase rspBase = MsgUtil.sendMsgWithBlocking(reqS0004);
                    if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
                        RspS0004 rspS0004 = (RspS0004) rspBase;
                        Message msg = new Message();
                        msg.what = 1;

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("safeCode", rspS0004.safeCode);
                        bundle.putSerializable("validityTime", rspS0004.validityTime);
                        validityDate = rspS0004.validityTime;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            } catch (BaseException e) { // TODO
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
