package view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lei.qrcode.LoginActivity;
import com.lei.qrcode.MainActivity;
import com.lei.qrcode.R;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.GetAsyncTask;


public class Resetpwd extends AppCompatActivity {

    private EditText mPwd_old;                        //密码编辑
    private EditText mPwd_new;                        //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Toolbar mResetPwdtoolbar;
    private TextView mResetWeak,mResetMid,mResetStr;
    //服务端地址
    final String resetpwdUrl = "/user/repassword";
    private SharedPreferences login_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        mPwd_old = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwd_new = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_check);

        mSureButton = (Button) findViewById(R.id.resetpwd_btn_sure);
        mResetWeak=findViewById(R.id.resetTextweak);
        mResetMid=findViewById(R.id.resetTextmid);
        mResetStr=findViewById(R.id.resetTextstr);

        mResetWeak.setBackgroundColor(Color.rgb(205,205,205));
        mResetMid.setBackgroundColor(Color.rgb(205,205,205));
        mResetStr.setBackgroundColor(Color.rgb(205,205,205));


        mResetPwdtoolbar=findViewById(R.id.resetpwd_toolbar);
        mResetPwdtoolbar.setTitle("重置密码");
        setSupportActionBar(mResetPwdtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        login_sp = getSharedPreferences("userInfo", 0);

        mSureButton.setOnClickListener(m_resetpwd_Listener);      //注册界面两个按钮的监听事件

        mPwd_new.addTextChangedListener(new TextWatcher() {
            private CharSequence word;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                word = charSequence;

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //resultTextView.setText("");
                String str = mPwd_new.getText().toString().trim();
                int length = str.length();
                if (length > 0) {
                    if (isChinese(str)) {
                        //str = str.substring(0, length - 1);
                        //mPwd.setText(str);
                        //String str1 = mPwd.getText().toString().trim();
                        //mPwd.setSelection(str1.length());
                        mPwd_new.setError("密码只能是字母和数字");
                    }
                }
                //输入框为0
                if (str.length() == 0)
                {
                    mResetWeak.setBackgroundColor(Color.rgb(205,205,205));
                    mResetMid.setBackgroundColor(Color.rgb(205,205,205));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的纯数字为弱
                if (str.matches ("^[0-9]+$"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(205,205,205));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的纯小写字母为弱
                else if (str.matches ("^[a-z]+$"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(205,205,205));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的纯大写字母为弱
                else if (str.matches ("^[A-Z]+$"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(205,205,205));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
//输入的大写字母和数字，输入的字符小于7个密码为弱
                else if (str.matches ("^[A-Z0-9]{1,5}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(205,205,205));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的大写字母和数字，输入的字符大于7个密码为中
                else if (str.matches ("^[A-Z0-9]{6,16}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(255,184,77));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的小写字母和数字，输入的字符小于7个密码为弱
                else if (str.matches ("^[a-z0-9]{1,5}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(205,205,205));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的小写字母和数字，输入的字符大于7个密码为中
                else if (str.matches ("^[a-z0-9]{6,16}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(255,184,77));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的大写字母和小写字母，输入的字符小于7个密码为弱
                else if (str.matches ("^[A-Za-z]{1,5}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(205,205,205));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的大写字母和小写字母，输入的字符大于7个密码为中
                else if (str.matches ("^[A-Za-z]{6,16}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(255,184,77));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的大写字母和小写字母和数字，输入的字符小于5个个密码为弱
                else if (str.matches ("^[A-Za-z0-9]{1,5}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(205,205,205));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的大写字母和小写字母和数字，输入的字符大于6个个密码为中
                else if (str.matches ("^[A-Za-z0-9]{6,8}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(255,184,77));
                    mResetStr.setBackgroundColor(Color.rgb(205,205,205));
                }
                //输入的大写字母和小写字母和数字，输入的字符大于8个密码为强
                else if (str.matches ("^[A-Za-z0-9]{9,16}"))
                {
                    mResetWeak.setBackgroundColor(Color.rgb(255,129,128));
                    mResetMid.setBackgroundColor(Color.rgb(255,184,77));
                    mResetStr.setBackgroundColor(Color.rgb(113,198,14));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputStr = editable.toString().trim();
                byte[] bytes = inputStr.getBytes();
                if (bytes.length > 16) {
                    Toast.makeText(Resetpwd.this, "超过规定字符数", Toast.LENGTH_SHORT).show();
                    Log.i("str", inputStr);
                    //取前15个字节
                    byte[] newBytes = new byte[16];
                    for (int i = 0; i < 16; i++) {
                        newBytes[i] = bytes[i];
                    }
                    String newStr = new String(newBytes);
                    mPwd_new.setText(newStr);
                    //将光标定位到最后
                    Selection.setSelection(mPwd_new.getEditableText(), newStr.length());
                }
            }
        });
    }
    private boolean isChinese(String str){
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.matches();
    }


    View.OnClickListener m_resetpwd_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.resetpwd_btn_sure:                       //确认按钮的监听事件
                    resetpwd_check();
                    break;
            }
        }
    };
    public void resetpwd_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);//返回1说明用户名和密码均正确
            //保存学号和密码
            final String studentId = pref.getString("STUDENTID","");
            String userPwd_old = mPwd_old.getText().toString().trim();
            final String userPwd_new = mPwd_new.getText().toString().trim();
            String userPwdCheck = mPwdCheck.getText().toString().trim();
                                           //返回1说明用户名和密码均正确,继续后续操作
                if(userPwd_new.equals(userPwdCheck)==false){           //两次密码输入不一样
                    Toast.makeText(this, getString(R.string.pwd_not_the_same),Toast.LENGTH_SHORT).show();
                    return ;
                } else {
                    try{
                    GetAsyncTask resetPwdAsyncTask = new GetAsyncTask();
                    resetPwdAsyncTask.setServerUrl(MainActivity.serverUrl+resetpwdUrl);
                    resetPwdAsyncTask.setStudent_id(studentId);
                    resetPwdAsyncTask.setOldPwd(userPwd_old);
                    resetPwdAsyncTask.setNewPwd(userPwd_new);
                    resetPwdAsyncTask.setOnDataFinishedListener(new GetAsyncTask.OnDataFinishedListener() {

                        @Override
                        public void onDataSuccessfully(Object s) {
                            try {
                                JSONObject jsonObject = new JSONObject((String) s);
                                String data = jsonObject.getString("data");
                                String msg = jsonObject.getString("msg");
                                Toast.makeText(Resetpwd.this, msg, Toast.LENGTH_SHORT).show();//登录提示
                                if (data != null && data.equals("true")) {
                                    //保存学号和密码
                                    SharedPreferences.Editor editor = login_sp.edit();
                                    editor.putString("STUDENTID", studentId);
                                    editor.putString("PASSWORD",userPwd_new);
                                    editor.commit();
                                    Intent intent = new Intent(Resetpwd.this, LoginActivity.class);    //切换Login Activity至User Activity
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onDataFailed() {
                            Toast.makeText(Resetpwd.this, "加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    resetPwdAsyncTask.execute();
                } catch (Exception e) {}
            }

        }
    }
    public boolean isUserNameAndPwdValid() {
        if (mPwd_old.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_new.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_new_empty),Toast.LENGTH_SHORT).show();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        //android.R.id.home对应应用程序图标的id
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
