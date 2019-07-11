package com.zzu.ybr.ybrsafety;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import android.support.v7.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ybr.Config;
import com.ybr.Constants;
import com.ybr.entity.MsgHead;
import com.ybr.entity.ent.User;
import com.ybr.entity.request.ReqS0002;
import com.ybr.entity.request.ReqS0100;
import com.ybr.entity.response.RspBase;
import com.ybr.entity.response.RspS0100;
import com.ybr.exception.BaseException;
import com.ybr.intfc.MsgCallBack;
import com.ybr.threading.ReqTaskThread;
import com.ybr.util.MsgUtil;

import org.w3c.dom.Text;


/**
 * 注册专用activity
 */
public class MainActivity extends AppCompatActivity
        {
       EditText username;
       EditText password;
       EditText email;
       Button btn_register;
       User user = new User();
       ReqS0002 reqS0002 = new ReqS0002();
       MainActivity tt = this;
       TextView login_text;
       EditText check_email;
       ImageView check_success;
       Boolean check_code =false;
       TextView comp_reg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        check_success = findViewById(R.id.check_success);
        check_email = findViewById(R.id.check_email);
        new Thread(new ReqTaskThread()).start();
        btn_register = findViewById(R.id.register);
        comp_reg = findViewById(R.id.comp_reg);
        login_text = findViewById(R.id.login_text);
        check_success.setVisibility(View.INVISIBLE);
        Config.NETServerIp="101.132.33.200";
        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        comp_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CompRegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(MainActivity.this,"注册成功！",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this,"验证码已经发送至您的邮箱",Toast.LENGTH_LONG);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("提示")
                            .setMessage("验证码已经发送至您的邮箱");
                    builder.create().show();
                    final String vcode_data = msg.getData().getString("vCode");
                    check_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus){
                                System.out.println("进入检测");
                                if (check_email.getText().toString().equals(vcode_data)){
                                    System.out.println("true");
                                    check_success.setVisibility(View.VISIBLE);
                                    check_code=true;

                                }else {
                                    check_success.setVisibility(View.INVISIBLE);
                                    check_code=false;

                                }
                            }
                        }
                    });
                    break;
                case 3:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                    builder2.setTitle("提示")
                            .setMessage("邮箱已被注册或请填写用户名与密码！");
                    builder2.create().show();
                    break;
                case 4:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("提示")
                            .setMessage("验证码错误或信息不能为空！");
                    builder1.create().show();
                    break;
                case 5:
                    AlertDialog.Builder builder3 =new AlertDialog.Builder(MainActivity.this);
                    builder3.setTitle("提示")
                            .setMessage("请输入正确的邮箱格式！")
                            ;
                    builder3.create().show();
                    break;

            }
        }
    };
    //点击注册按钮
    public void onClick(View v) {
        user.email=email.getText().toString();
        user.name = username.getText().toString();
        user.password = password.getText().toString();

        reqS0002.user = user;
        if (check_code==true&&!email.getText().toString().equals("")&&!username.getText().toString().equals("")&&!password.getText().toString().equals("")) {
            try {
                //跳转 回调
                MsgUtil.sendMsg(reqS0002, new MsgCallBack() {
                    @Override
                    public void callback(MsgHead msgHead, RspBase rspBase) {
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = rspBase;
                        handler.sendMessage(msg);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            } catch (BaseException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            Message msg2 = new Message();
            msg2.what=4;
            handler.sendMessage(msg2);
        }
    }
    //发送邮箱验证码请求
    public void send_check(View view){
            System.out.println("进入发送验证码请求中");
            //首先检查邮箱格式是否正确
            if (isEmail(email.getText().toString())) {
                ReqS0100 reqS0100 = new ReqS0100();
                reqS0100.vCodeType = "1";
                reqS0100.email = email.getText().toString();
                System.out.println("value:" + reqS0100.email + " " + reqS0100.vCodeType);
                try {
                    MsgUtil.sendMsg(reqS0100, new MsgCallBack() {
                        @Override
                        public void callback(MsgHead msgHead, RspBase rspBase) {
                            if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
                                RspS0100 rspS0100 = (RspS0100) rspBase;
                                String vCode = rspS0100.vCode;
                                System.out.println("vcode:" + vCode);
                                Message msg = new Message();
                                msg.what = 2;
                                Bundle bundle = new Bundle();
                                bundle.putString("vCode", vCode);
                                System.out.println("vcode1:" + vCode);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            } else {
                                Message msg_error = new Message();
                                msg_error.what = 3;
                                handler.sendMessage(msg_error);
                            }
                        }
                    });
                } catch (BaseException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                Message msgg =new Message();
                msgg.what=5;
                handler.sendMessage(msgg);
            }

            }
    //检查邮箱格式是否正确
    public boolean isEmail(String e) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return e.matches(strPattern);
        }
    }



}
