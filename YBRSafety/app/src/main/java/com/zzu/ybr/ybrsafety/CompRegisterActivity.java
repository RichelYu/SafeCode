package com.zzu.ybr.ybrsafety;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ybr.Config;
import com.ybr.Constants;
import com.ybr.entity.MsgHead;
import com.ybr.entity.ent.Comp;
import com.ybr.entity.request.ReqS2001;
import com.ybr.entity.response.RspBase;
import com.ybr.entity.response.RspS0100;
import com.ybr.entity.response.RspS2001;
import com.ybr.exception.BaseException;
import com.ybr.intfc.MsgCallBack;
import com.ybr.threading.ReqTaskThread;
import com.ybr.util.MsgUtil;

public class CompRegisterActivity extends AppCompatActivity {
    EditText comp_name;
    EditText comp_host;
    Button btn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comp_register);
        comp_host = findViewById(R.id.com_host);
        comp_name = findViewById(R.id.comp_name);
        btn = findViewById(R.id.comp_reg_btn);
        new Thread(new ReqTaskThread()).start();
        Config.NETServerIp="101.132.33.200";

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    AlertDialog.Builder a = new AlertDialog.Builder(CompRegisterActivity.this);
                    a.setTitle("注册成功！")
                            .setMessage("公司key是："+msg.getData().getString("comp_key")+"  "+"请牢记")
                            .create().show();
            }
        }
    };
    public void onClick_comp_reg(View v){
        ReqS2001 reqS2001 = new ReqS2001();
        reqS2001.compName = comp_name.getText().toString();
        reqS2001.compHost = comp_host.getText().toString();
        if (!comp_name.getText().toString().equals("")&&!comp_host.getText().toString().equals("")){
            try {
                MsgUtil.sendMsg(reqS2001, new MsgCallBack() {
                    @Override
                    public void callback(MsgHead msgHead, RspBase rspBase) {
                        if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)){
                            RspS2001 rspS2001 = (RspS2001)rspBase;
                            String comp_key = rspS2001.compKey;
                            Message msg = new Message();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("comp_key",comp_key);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }else {
                            System.out.print("公司名称或host不能为空");
                        }
                    }
                });
            } catch (BaseException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            Message msg = new Message();
            msg.what = 2;
            handler.sendMessage(msg);

        }



    }
}
