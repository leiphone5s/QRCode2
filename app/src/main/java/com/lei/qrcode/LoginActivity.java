package com.lei.qrcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import common.GetAsyncTask;
import activity.GetbackPwd;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public int pwdresetFlag=0;
    private EditText mStudentId;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button mRegisterButton;                   //注册按钮
    private Button mLoginButton;                      //登录按钮
    private CheckBox mRememberCheck;

    private SharedPreferences login_sp;
    private String userNameValue,passwordValue;

    private View loginView;                           //登录
    private View loginSuccessView;
    private TextView loginSuccessShow;
    private TextView mForgetpwdText;
    private final String loginUrl = "/user/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        //通过id找到相应的控件
        mStudentId = (EditText) findViewById(R.id.login_edit_account);
        mStudentId.requestFocus();
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);
        mRegisterButton = (Button) findViewById(R.id.login_btn_register);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);
        loginView=findViewById(R.id.login_view);
        loginSuccessView=findViewById(R.id.login_success_view);
        loginSuccessShow=(TextView) findViewById(R.id.login_success_show);

        mForgetpwdText = (TextView) findViewById(R.id.forget_text_change_pwd);

        mRememberCheck = (CheckBox) findViewById(R.id.Login_Remember);

        login_sp = getSharedPreferences("userInfo", 0);
        boolean choseRemember =login_sp.getBoolean("mRememberCheck", false);
        boolean choseAutoLogin =login_sp.getBoolean("mAutologinCheck", false);

        mRegisterButton.setOnClickListener(mListener);                      //采用OnClickListener方法设置不同按钮按下之后的监听事件
        mLoginButton.setOnClickListener(mListener);
        //mCancleButton.setOnClickListener(mListener);
        mForgetpwdText.setOnClickListener(mListener);

        ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
        image.setImageResource(R.mipmap.toppic);
        //保存学号和密码
        boolean isSavePwd = login_sp.getBoolean("mRememberCheck",false);
        String studentId = login_sp.getString("STUDENTID","");
        String password = login_sp.getString("PASSWORD","");
        if(studentId.length()!=0){
            mStudentId.setText(login_sp.getString("STUDENTID",""));
        }else{
            mStudentId.setText("");
        }
        if(isSavePwd){
            mPwd.setText(password);
            mRememberCheck.setChecked(true);
        }else{
            mPwd.setText("");
            mRememberCheck.setChecked(false);
        }

    }

    OnClickListener mListener = new OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_register:                            //登录界面的注册按钮
                    Intent intent_Login_to_Register = new Intent(LoginActivity.this,RegisterActivity.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_Register);
                    finish();
                    break;
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    login();
                    break;
                case R.id.forget_text_change_pwd:                             //登录界面的忘记密码按钮
                    Intent intent_Login_to_reset = new Intent(LoginActivity.this, GetbackPwd.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_Login_to_reset);
                    break;
            }
        }
    };

    public void login() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            final String student_id = mStudentId.getText().toString().trim();    //获取当前输入的用户名和密码信息
            final String userPwd = mPwd.getText().toString().trim();

            try {
                GetAsyncTask loginAsyncTask = new GetAsyncTask();
                loginAsyncTask.setServerUrl(MainActivity.serverUrl+loginUrl);
                loginAsyncTask.setStudent_id(student_id);
                loginAsyncTask.setPwd(userPwd);
                loginAsyncTask.setOnDataFinishedListener(new GetAsyncTask.OnDataFinishedListener() {

                    @Override
                    public void onDataSuccessfully(Object s) {
                        try {
                            JSONObject jsonObject = new JSONObject((String)s);
                            String data = jsonObject.getString("data");
                            String msg = jsonObject.getString("msg");
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();//登录提示
                            if(data !=null && !data.equals("false")) {
                                JSONObject jObject = new JSONObject(data);
                                //保存学号和密码
                                SharedPreferences.Editor editor = login_sp.edit();
                                editor.putString("STUDENTID", jObject.get("student_no").toString());
                                editor.putString("USERNAME", jObject.get("username").toString());
                                editor.putString("EMAIL",jObject.get("email").toString());
                                editor.putString("PHONE",jObject.get("phone").toString());
                                editor.putString("PASSWORD",userPwd);
                                editor.putBoolean("mRememberCheck",mRememberCheck.isChecked());
                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);    //切换Login Activity至User Activity
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDataFailed() {
                        Toast.makeText(LoginActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                loginAsyncTask.execute();
            }catch(Exception e){

            }

        }

    }


    public boolean isUserNameAndPwdValid() {
        if (mStudentId.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onResume() {

        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

}

