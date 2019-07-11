package com.zzu.ybr.ybrsafety;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ybr.Config;

import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.entity.MsgHead;
import com.ybr.entity.ent.User;
import com.ybr.entity.request.ReqS0003;
import com.ybr.entity.request.ReqS0011;
import com.ybr.entity.request.ReqS0100;
import com.ybr.entity.response.RspBase;
import com.ybr.entity.response.RspS0003;
import com.ybr.entity.response.RspS0100;
import com.ybr.exception.BaseException;
import com.ybr.intfc.MsgCallBack;
import com.ybr.threading.ReqTaskThread;
import com.ybr.util.MsgUtil;


/**
 * 登陆
 */
public class LoginActivity extends AppCompatActivity{

    EditText password;
    EditText email;
    Button btn_login;
    TextView new_register;
    ReqS0003 reqS0003= new ReqS0003();
    User user = new User();
    TextView service_content;
    SharedPreferences sharedPreferences;
    CheckBox remember_psd;
    CheckBox auto_login;
    TextView forget_psd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.NETServerIp="101.132.33.200";
        //加载布局文件前判断是否登陆过
        sharedPreferences = getSharedPreferences("userinfo",0);
//        spediter=sharedPreferences.edit();
        //找不到main返回的键值是返回false
        if(sharedPreferences.getBoolean("autol",true)){
            ReqS0003 reqS0003 = new ReqS0003();
            User user1 = new User();
            String email1 = sharedPreferences.getString("e_mail","");
            final String psd1 = sharedPreferences.getString("psd","");
            user1.email = email1;
            user1.password = psd1;
            reqS0003.user = user1;
            try {
                //登陆跳转 回调
                MsgUtil.sendMsg(reqS0003, new MsgCallBack() {
                    @Override
                    public void callback(MsgHead msgHead, RspBase rspBase) {
                        if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
                            RspS0003 rspS0003 = (RspS0003) rspBase;
                            System.out.println(rspBase.rspMsg);
                            VarCache.rootID = rspS0003.rootID;
                            VarCache.clientID = rspS0003.user.id;
                            Message msg = handler.obtainMessage();
                            msg.obj = rspBase;
                            handler.sendMessage(msg);
                            Intent intent = new Intent();
                            intent.putExtra("username",rspS0003.user.name);
                            intent.putExtra("id",rspS0003.user.id);
                            intent.putExtra("password",psd1);
                            intent.putExtra("key",rspS0003.user.key);
                            intent.setClass(LoginActivity.this,SafeCodeActivity.class);
                            startActivity(intent);

                        }else{

                        }
                    }
                });
            } catch (BaseException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setContentView(R.layout.login);
        forget_psd = findViewById(R.id.forget_pwd);
        auto_login = findViewById(R.id.auto_login);
        remember_psd = findViewById(R.id.remember_psd);
        sharedPreferences = getSharedPreferences("userinfo",0);
        String e_mail = sharedPreferences.getString("e_mail","");
        String psd = sharedPreferences.getString("psd","");
        boolean chooseRem = sharedPreferences.getBoolean("rem",false);
        boolean chooseAutoL=sharedPreferences.getBoolean("autol",false);
        btn_login = (Button) findViewById(R.id.login_btn);
        new_register = findViewById(R.id.new_register);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        //checkbox联动
        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    remember_psd.setChecked(true);
                    remember_psd.setClickable(false);
                }else {
                    remember_psd.setClickable(true);
                }
            }
        });

        //如果上次勾选了记住密码，那么进入登陆页面也自动勾选记住密码，并自动填入用户名和密码
        if (chooseRem){
            email.setText(e_mail);
            password.setText(psd);
            remember_psd.setChecked(true);

        }
        //如果上次选择了自动登陆，那么进入登陆界面也勾选自动登陆
        if (chooseAutoL){
            email.setText(e_mail);
            password.setText(psd);
            remember_psd.setChecked(true);
            auto_login.setChecked(true);
        }
        new Thread(new ReqTaskThread()).start();
        //新用户注册
        new_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        service_content = findViewById(R.id.service_content);
        //服务条款
        service_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("服务条款")
                        .setMessage("具体服务条款请等待后续更新");
                builder.create().show();

            }
        });


    }



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(LoginActivity.this,"登陆成功！",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("提示")
                            .setMessage("邮箱或密码错误");
                    builder.create().show();
                    break;
                case 3:
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(LoginActivity.this);
                    builder3.setTitle("提示")
                            .setMessage("重置密码成功");
                    builder3.create().show();
                    break;
                case 4:
                    AlertDialog.Builder builder4 = new AlertDialog.Builder(LoginActivity.this);
                    builder4.setTitle("提示")
                            .setMessage("重置密码失败（验证码错误）");
                    builder4.create().show();
                    break;
                case 5:
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(LoginActivity.this);
                    builder5.setTitle("提示")
                            .setMessage("邮箱格式不正确！");
                    builder5.create().show();
                    break;
                case 6:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setTitle("提示")
                            .setMessage("验证码已发送至您的邮箱");
                    builder1.create().show();
            }
        }
    };

    //登陆
    public void onClick_login(View v) {
        user.email = email.getText().toString();
        user.password = password. getText().toString();
        reqS0003.user= user;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("e_mail",email.getText().toString());
        editor.putString("psd",password.getText().toString());
        //是否记住密码
        if (remember_psd.isChecked()){
            editor.putBoolean("rem",true);
        }else {
            editor.putBoolean("rem",false);
        }
        //是否自动登陆
        if (auto_login.isChecked()){
            editor.putBoolean("autol",true);
        }else {
            editor.putBoolean("autol",false);
        }
        editor.commit();
        try {
            //登陆跳转 回调
            MsgUtil.sendMsg(reqS0003, new MsgCallBack() {
                @Override
                public void callback(MsgHead msgHead, RspBase rspBase) {
                    if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)) {
                        RspS0003 rspS0003 = (RspS0003) rspBase;
                        System.out.println(rspBase.rspMsg);
                        VarCache.rootID = rspS0003.rootID;
                        VarCache.clientID = rspS0003.user.id;

                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = rspBase;
                        handler.sendMessage(msg);
                        Intent intent = new Intent();
                        intent.putExtra("username",rspS0003.user.name);
                        intent.putExtra("id",rspS0003.user.id);
                        intent.putExtra("password",password.getText().toString());
                        intent.putExtra("key",rspS0003.user.key);
                        intent.setClass(LoginActivity.this,SafeCodeActivity.class);
                        startActivity(intent);

                    }else{
                        System.out.println("错误信息："+rspBase.rspCode+""+rspBase.rspMsg);
                        Message fail = new Message();
                        fail.what = 2;
                        handler.sendMessage(fail);

                    }
                }
            });
        } catch (BaseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //忘记密码
    public void forget_pwd(View view) {
        AlertDialog.Builder input = new AlertDialog.Builder(LoginActivity.this);
        final String[] sec_code = new String[1];
        View forget_psd_view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.forget_psd,null);
        Button get_sec_code = forget_psd_view.findViewById(R.id.get_sec_code);//获取验证码按钮
        final EditText f_email = forget_psd_view.findViewById(R.id.f_email);//验证邮箱填写框
        final EditText f_psd = forget_psd_view.findViewById(R.id.f_sec_code);//验证码填写框
        input.setTitle("邮箱验证")
                .setView(forget_psd_view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println(sec_code[0]);
                        if (sec_code[0].equals(f_psd.getText().toString())){
                            AlertDialog.Builder edit_psd = new AlertDialog.Builder(LoginActivity.this);
                            View f_edit_psd = LayoutInflater.from(LoginActivity.this).inflate(R.layout.forget_edit_psd,null);
                            final EditText f_new_psd = f_edit_psd.findViewById(R.id.f_new_psd);
                            edit_psd.setTitle("重置密码")
                                    .setView(f_edit_psd)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        //重置密码
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ReqS0011 reqS0011 = new ReqS0011();
                                            reqS0011.email = f_email.getText().toString();
                                            reqS0011.password = f_new_psd.getText().toString();
                                            try {
                                                MsgUtil.sendMsg(reqS0011, new MsgCallBack() {
                                                    @Override
                                                    public void callback(MsgHead msgHead, RspBase rspBase) {
                                                        //重置密码成功
                                                        if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)){
                                                            Message msg0011suc = new Message();
                                                            msg0011suc.what=3;
                                                            handler.sendMessage(msg0011suc);
                                                        }
                                                        else {
                                                            //重置密码失败
                                                            Message msg0011fail = new Message();
                                                            msg0011fail.what=4;
                                                            handler.sendMessage(msg0011fail);

                                                        }
                                                        }
                                                });
                                            } catch (BaseException e) {
                                                e.printStackTrace();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            edit_psd.create().show();
                        }
                        else {
                            Message msgfail = new Message();
                            msgfail.what=4;
                            handler.sendMessage(msgfail);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        //获取邮箱验证码
        get_sec_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("enter get_sec_code method!");
                if (isEmail(f_email.getText().toString())){
                    ReqS0100 reqS0100 = new ReqS0100();
                    reqS0100.vCodeType="2";
                    reqS0100.email = f_email.getText().toString();
                    try {
                        MsgUtil.sendMsg(reqS0100, new MsgCallBack() {
                            @Override
                            public void callback(MsgHead msgHead, RspBase rspBase) {
                                    if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)){
                                        RspS0100 rspS0100 =(RspS0100)rspBase;
                                        sec_code[0] = rspS0100.vCode;
                                        Message msg0100 = new Message();
                                        msg0100.what = 6;
                                        handler.sendMessage(msg0100);
                                    }else {
                                        System.out.println("rspBase.rspCode!=RSP CODE SUCCESS");
                                    }
                            }
                        });
                    } catch (BaseException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else {
                    Message msg5 = new Message();
                    msg5.what=5;
                    handler.sendMessage(msg5);
                }

            }
        });
        input.create().show();




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
