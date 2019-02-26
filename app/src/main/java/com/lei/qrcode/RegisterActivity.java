package com.lei.qrcode;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import activity.RegisterSelectWheel2;
import common.GetAsyncTask;
import activity.RegisterSelectWheel;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private EditText mStuentId;
    private TextView textWeak, textMid, textStr;
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private Toolbar mRegistertoolbar;
    private RadioButton mRadioStudent, mRadioTeacher;
    private SharedPreferences login_sp;
    private TextView adresss;
    private final static int REQUESTCODE = 1; // 返回的结果码

    //服务端地址
    final String registerUrl = "/user/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);

        mStuentId = (EditText) findViewById(R.id.student_id);

        mSureButton = (Button) findViewById(R.id.register_btn_sure);

        mRadioStudent = (RadioButton) findViewById(R.id.radioButton1);
        mRadioTeacher = (RadioButton) findViewById(R.id.radioButton2);

        adresss = findViewById(R.id.register_address);

        mRegistertoolbar = findViewById(R.id.register_toolbar);
        mRegistertoolbar.setTitle("注册");

        textWeak = findViewById(R.id.resultTextweak);
        textMid = findViewById(R.id.resultTextmid);
        textStr = findViewById(R.id.resultTextstr);
        textWeak.setBackgroundColor(Color.rgb(205, 205, 205));
        textMid.setBackgroundColor(Color.rgb(205, 205, 205));
        textStr.setBackgroundColor(Color.rgb(205, 205, 205));


        login_sp = getSharedPreferences("userInfo", 0);
        setSupportActionBar(mRegistertoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件

        mPwd.addTextChangedListener(registerTextWatcher);

        adresss.setOnClickListener(m_register_Listener);

    }

    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    register_check();
                    break;
                case R.id.register_address:
                    // 意图实现activity的跳转
                    if(mRadioTeacher.isChecked()){
                        Intent intent = new Intent(RegisterActivity.this, RegisterSelectWheel2.class);
                        intent.putExtra("a", 1);
                        startActivityForResult(intent, REQUESTCODE); //REQUESTCODE--->1
                    }else{
                        Intent intent = new Intent(RegisterActivity.this, RegisterSelectWheel.class);
                        intent.putExtra("a", 1);
                        startActivityForResult(intent, REQUESTCODE); //REQUESTCODE--->1
                    }

                    break;
            }
        }
    };

    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            final String userName = mAccount.getText().toString().trim();
            final String student_id = mStuentId.getText().toString().trim();
            final String userPwd = mPwd.getText().toString().trim();
            final String userPwdCheck = mPwdCheck.getText().toString().trim();
            final boolean isTeacher = mRadioTeacher.isChecked();
            final boolean isStudent = mRadioStudent.isChecked();
            Log.d("lei", "isBoy = " + isTeacher + "    isGirl = " + isStudent);
            if (userPwd.equals(userPwdCheck)) {
                try {
                    GetAsyncTask registerAsyncTask = new GetAsyncTask();
                    registerAsyncTask.setServerUrl(MainActivity.serverUrl+registerUrl);
                    registerAsyncTask.setStudent_id(student_id);
                    registerAsyncTask.setUsername(userName);
                    registerAsyncTask.setPwd(userPwd);
                    registerAsyncTask.setOnDataFinishedListener(new GetAsyncTask.OnDataFinishedListener() {

                        @Override
                        public void onDataSuccessfully(Object s) {
                            try {
                                JSONObject jsonObject = new JSONObject((String) s);
                                String data = jsonObject.getString("data");
                                String msg = jsonObject.getString("msg");
                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();//登录提示
                                if (data != null && data.equals("true")) {
                                    //保存学号和密码
                                    SharedPreferences.Editor editor = login_sp.edit();
                                    editor.putString("STUDENTID", student_id);
                                    editor.putString("USERNAME", userName);
                                    editor.putString("PASSWORD", userPwd);
                                    editor.commit();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);    //切换Login Activity至User Activity
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onDataFailed() {
                            Toast.makeText(RegisterActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    registerAsyncTask.execute();
                } catch (Exception e) {

                }
            } else {
                Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        //android.R.id.home对应应用程序图标的id
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mStuentId.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.studentId_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    TextWatcher registerTextWatcher = new TextWatcher() {
        private CharSequence word;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            word = charSequence;

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //resultTextView.setText("");
            String str = mPwd.getText().toString().trim();
            int length = str.length();
            if (length > 0) {
                if (isChinese(str)) {
                    //str = str.substring(0, length - 1);
                    //mPwd.setText(str);
                    //String str1 = mPwd.getText().toString().trim();
                    //mPwd.setSelection(str1.length());
                    mPwd.setError("密码只能是字母和数字");
                }
            }
            //输入框为0
            if (str.length() == 0) {
                textWeak.setBackgroundColor(Color.rgb(205, 205, 205));
                textMid.setBackgroundColor(Color.rgb(205, 205, 205));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的纯数字为弱
            if (str.matches("^[0-9]+$")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(205, 205, 205));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的纯小写字母为弱
            else if (str.matches("^[a-z]+$")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(205, 205, 205));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的纯大写字母为弱
            else if (str.matches("^[A-Z]+$")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(205, 205, 205));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
//输入的大写字母和数字，输入的字符小于7个密码为弱
            else if (str.matches("^[A-Z0-9]{1,5}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(205, 205, 205));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的大写字母和数字，输入的字符大于7个密码为中
            else if (str.matches("^[A-Z0-9]{6,16}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(255, 184, 77));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的小写字母和数字，输入的字符小于7个密码为弱
            else if (str.matches("^[a-z0-9]{1,5}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(205, 205, 205));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的小写字母和数字，输入的字符大于7个密码为中
            else if (str.matches("^[a-z0-9]{6,16}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(255, 184, 77));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的大写字母和小写字母，输入的字符小于7个密码为弱
            else if (str.matches("^[A-Za-z]{1,5}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(205, 205, 205));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的大写字母和小写字母，输入的字符大于7个密码为中
            else if (str.matches("^[A-Za-z]{6,16}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(255, 184, 77));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的大写字母和小写字母和数字，输入的字符小于5个个密码为弱
            else if (str.matches("^[A-Za-z0-9]{1,5}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(205, 205, 205));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的大写字母和小写字母和数字，输入的字符大于6个个密码为中
            else if (str.matches("^[A-Za-z0-9]{6,8}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(255, 184, 77));
                textStr.setBackgroundColor(Color.rgb(205, 205, 205));
            }
            //输入的大写字母和小写字母和数字，输入的字符大于8个密码为强
            else if (str.matches("^[A-Za-z0-9]{9,16}")) {
                textWeak.setBackgroundColor(Color.rgb(255, 129, 128));
                textMid.setBackgroundColor(Color.rgb(255, 184, 77));
                textStr.setBackgroundColor(Color.rgb(113, 198, 14));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String inputStr = editable.toString().trim();
            byte[] bytes = inputStr.getBytes();
            if (bytes.length > 16) {
                Toast.makeText(RegisterActivity.this, "超过规定字符数", Toast.LENGTH_SHORT).show();
                Log.i("str", inputStr);
                //取前15个字节
                byte[] newBytes = new byte[16];
                for (int i = 0; i < 16; i++) {
                    newBytes[i] = bytes[i];
                }
                String newStr = new String(newBytes);
                mPwd.setText(newStr);
                //将光标定位到最后
                Selection.setSelection(mPwd.getEditableText(), newStr.length());
            }
        }

    };

    // 为了获取结果
    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == 2) {
            if (requestCode == REQUESTCODE) {
                String mFaculty = data.getStringExtra("faculty");
                String mDepart = data.getStringExtra("depart");
                String mClass = data.getStringExtra("class");
                //设置结果显示框的显示数值
                if(mClass != null) {
                    adresss.setText("河南理工大学" + mFaculty + mDepart + mClass);
                }else{
                    adresss.setText("河南理工大学" + mFaculty + mDepart);
                }
            }
        }
    }


}

