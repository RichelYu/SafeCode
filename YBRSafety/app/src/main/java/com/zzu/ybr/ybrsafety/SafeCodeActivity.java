package com.zzu.ybr.ybrsafety;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ybr.Config;
import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.entity.MsgHead;
import com.ybr.entity.ent.Comp;
import com.ybr.entity.request.ReqS0005;
import com.ybr.entity.request.ReqS0007;
import com.ybr.entity.request.ReqS0008;
import com.ybr.entity.request.ReqS0009;
import com.ybr.entity.request.ReqS0010;
import com.ybr.entity.response.RspBase;
import com.ybr.entity.response.RspS0005;
import com.ybr.entity.response.RspS0009;
import com.ybr.exception.BaseException;
import com.ybr.intfc.MsgCallBack;
import com.ybr.util.MsgUtil;
import com.zzu.ybr.ybrsafety.thread.SafeCodeTimer;
import com.zzu.ybr.ybrsafety.util.InitClient;
import java.util.ArrayList;


public class SafeCodeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    static {
        InitClient.init();
    }
    TextView show ;
    ProgressBar progressBar;
    TextView remainTime;
    ReqS0007 reqS0007 = new ReqS0007();
    String username;
    String password;
    String userid;
    String key;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SafeCodeTimer scTimer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_code);
        Config.NETServerIp="101.132.33.200";
        show = findViewById(R.id.show_code);
        progressBar = findViewById(R.id.progress_bar);
        remainTime = findViewById(R.id.remainTime);
        setProgressBarVisibility(true);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userid = intent.getStringExtra("id");
        password = intent.getStringExtra("password");
        key = intent.getStringExtra("key");
        System.out.println("用户名:"+username+""+"密码:"+password);
//        String sInfoFormat = getResources().getString(R.string.nav_header_title);
//        System.out.println("字符串："+sInfoFormat);
//        sInfoFormat=String.format(sInfoFormat,"oiewrjioq");


        /**
         * 上边栏
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_get_code);
        setSupportActionBar(toolbar);
        /**
         * 主界面
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.get_code_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //服务器时间同步
        try {
            scTimer = new SafeCodeTimer(handler);
            new Thread(scTimer,"SafeCodeTimer").start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    final Handler handler = new Handler(){ // 此handler对应线程SafeCodeThread类 ps..如要修改，需同步修改
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0: // 刷新进度条
                    int progress = msg.getData().getInt("progress");
                    progressBar.setProgress(progress);
                    remainTime.setText(15-(int)progress / 1000 + "秒后刷新安全码");
                    break;
                case 1:
                    show.setText(msg.getData().getCharSequence("safeCode"));
                    break;
                case 2:
                    AlertDialog.Builder showComp = new AlertDialog.Builder(SafeCodeActivity.this);
                    final View edit_bind = LayoutInflater.from(SafeCodeActivity.this).inflate(R.layout.edit_bind,null);
                    LinearLayout content = edit_bind.findViewById(R.id.edit_bind_content);
//公司填充
                    ArrayList<Comp> comps = (ArrayList) msg.getData().getSerializable("comps");
                    TextView textView = new TextView(SafeCodeActivity.this);
                    Button btn  = new Button(SafeCodeActivity.this);
                    //设置权重 TextView权重
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,3.5f);
                    //btn权重
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
                    textView.setLayoutParams(layoutParams1);
                    btn.setLayoutParams(layoutParams2);
                    LinearLayout single = new LinearLayout(SafeCodeActivity.this);
                    single.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                    single.setMinimumHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                    single.setOrientation(LinearLayout.HORIZONTAL);
                    for (final Comp comp:comps){
                        textView.setText(comp.compName);
                        btn.setText("解绑");
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ReqS0008 reqS0008 = new ReqS0008();
                                reqS0008.compKey = comp.compKey;
                                try {
                                    MsgUtil.sendMsg(reqS0008, new MsgCallBack() {
                                        @Override
                                        public void callback(MsgHead msgHead, RspBase rspBase) {
                                            if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)){
                                                Message message = new Message();
                                                message.what=6;
                                                handler.sendMessage(message);
                                            }
                                        }
                                    });
                                } catch (BaseException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        single.addView(textView);
                        single.addView(btn);
                        content.addView(single);
                    }
                    showComp.setTitle("绑定信息")
                            .setView(edit_bind)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create().show();
                        break;
                case 3:
                    System.out.println("进入case3");
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SafeCodeActivity.this);
                    builder1.setTitle("提示")
                        .setMessage("对外Key已变更")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder1.create().show();
                    //变更结束后进行登出操作
                    logout();
                    break;
                case 4:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SafeCodeActivity.this);
                    builder2.setTitle("提示")
                            .setMessage("密码已修改")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder2.create().show();
                    logout();
                    break;
                case 5:
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(SafeCodeActivity.this);
                    builder3.setTitle("提示")
                            .setMessage("原密码不正确！")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder3.create().show();
                    break;
                case 6:
                    AlertDialog.Builder builder4 = new AlertDialog.Builder(SafeCodeActivity.this);
                    builder4.setTitle("提示")
                            .setMessage("解绑成功！请重新查询！")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder4.create().show();
                    break;

            }
        }

    };
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.get_code_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//菜单栏onCreate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //注销
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                sharedPreferences= getSharedPreferences("userinfo",0);
                editor=sharedPreferences.edit();
                editor.putBoolean("rem",false);
                editor.putBoolean("autol",false);
                editor.putString("e_mail","");
                editor.putString("psd","");
                editor.commit();
                System.out.println("click logout");
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //修改密码
        if (id == R.id.nav_modify){
            AlertDialog.Builder input = new AlertDialog.Builder(this);
            final View edit_psd = LayoutInflater.from(SafeCodeActivity.this).inflate(R.layout.edit_psd,null);
            final EditText old = edit_psd.findViewById(R.id.old_psd);
            final EditText new_psd = edit_psd.findViewById(R.id.new_psd);
            input.setTitle("修改密码")
                    .setView(edit_psd)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ReqS0010 reqS0010 = new ReqS0010();
                            reqS0010.uid = userid;
                            reqS0010.oldPassword = old.getText().toString();
                            reqS0010.newPassword = new_psd.getText().toString();
                            final Message msg0010 = new Message();
                            try {
                                MsgUtil.sendMsg(reqS0010, new MsgCallBack() {
                                    @Override
                                    public void callback(MsgHead msgHead, RspBase rspBase) {
                                       if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)){

                                            msg0010.what =4;
                                            handler.sendMessage(msg0010);
                                       }
                                       else {
                                           msg0010.what = 5;
                                           handler.sendMessage(msg0010);
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
            input.create().show();
    //获取KEY
        }else if (id == R.id.nav_getid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示")
                    .setMessage("您的绑定KEY是："+key);
            builder.create().show();
    //绑定查询&解绑
        }else if (id == R.id.nav_unbind){
            ReqS0009 reqS0009 = new ReqS0009();
            try {
                MsgUtil.sendMsg(reqS0009, new MsgCallBack() {
                    @Override
                    public void callback(MsgHead msgHead, RspBase rspBase) {
                        RspS0009 rspS0009 =(RspS0009) rspBase;
                        ArrayList comps = rspS0009.comps;
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("comps",comps);
                        msg.setData(bundle);
                        msg.what=2;
                        handler.sendMessage(msg);
                    }
                });
            } catch (BaseException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    //变更对外key
        }else if(id == R.id.nav_change){
            System.out.println("进入变更对外KEY");
            ReqS0005 reqS0005 = new ReqS0005();
            reqS0005.userId=userid;
            reqS0005.password=password;
            try {
                MsgUtil.sendMsg(reqS0005, new MsgCallBack() {
                    @Override
                    public void callback(MsgHead msgHead, RspBase rspBase) {
                        if (rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)){
 //                           RspS0005 rspS0005 = (RspS0005)rspBase;
//                            userid=rspS0005.userKey;
                            //变更完用户key后再进行解绑所有公司

                            Message msg = new Message();
                            msg.what=3;
                            handler.sendMessage(msg);

                        }
                        else{
                            System.out.println("error");
                        }
                    }
                });
            } catch (BaseException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println("进入try");
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.get_code_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public  void  logout(){
        try {
            //关闭安全码线程
            scTimer.stopTimer();
            MsgUtil.sendMsg(reqS0007, new MsgCallBack() {
                @Override
                public void callback(MsgHead msgHead, RspBase rspBase) {
                    if(rspBase.rspCode.equals(Constants.RSP_CODE_SUCCESS)){
                        VarCache.rootID="";
                        VarCache.clientID="";
                        Intent intent = new Intent(SafeCodeActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }

                }
            });
        } catch (BaseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
