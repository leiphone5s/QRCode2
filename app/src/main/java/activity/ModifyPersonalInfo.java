package activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lei.qrcode.MainActivity;
import com.lei.qrcode.R;

import org.json.JSONObject;

import common.GetAsyncTask;

public class ModifyPersonalInfo extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText mEditUni,mEditFal,mEditUser,mEditEmail,mEditCal;
    private SharedPreferences modify_sp;
    private Button mSaveInfo;
    private String mUserName;
    private String mUserEmail;
    private String mUserPhone;
    private String mUserSchool;
    private String mUserFaculty;
    //服务端地址
    final String improveInfoUrl = "/user/updateUserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);
        toolbar=findViewById(R.id.improveinfo_toolbar);
        mEditUni=findViewById(R.id.improveinfo_edit_university);
        mEditFal=findViewById(R.id.improveinfo_edit_colleage);
        mEditUser=findViewById(R.id.improveinfo_edit_user);
        mEditEmail=findViewById(R.id.improveinfo_edit_email);
        mEditCal=findViewById(R.id.improveinfo_edit_call);

        mSaveInfo=findViewById(R.id.improve_btn_sure);
        mSaveInfo.setOnClickListener(mSaveInfoListener);
        toolbar.setTitle("个人信息");
        modify_sp = getSharedPreferences("userInfo", 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //保存学号和密码
        //String userinfo = pref.getString("UserInfo","");
        try {

            mUserSchool = "河南理工大学";
            mUserFaculty="计算机学院";
            mUserName = modify_sp.getString("USERNAME","");
            mUserEmail = modify_sp.getString("EMAIL","");
            mUserPhone = modify_sp.getString("PHONE","");
            mEditUni.setText(mUserSchool);
            mEditFal.setText(mUserFaculty);
            mEditUser.setText(mUserName);
            mEditEmail.setText(mUserEmail);
            mEditCal.setText(mUserPhone);

            mEditUni.setEnabled(false);
            mEditFal.setEnabled(false);
        }catch(Exception e){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    View.OnClickListener mSaveInfoListener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.improve_btn_sure:                       //确认按钮的监听事件
                    saveUserInfo();
                    break;
            }
        }
    };

    private void saveUserInfo(){
        final String studentId = modify_sp.getString("STUDENTID","");
        final String email = mEditEmail.getText().toString().trim();
        final String phone = mEditCal.getText().toString().trim();
        final String user = mEditUser.getText().toString().trim();
        if(isEmailAndPhoneAndUserValid()) {
            GetAsyncTask getAsyncTask=new GetAsyncTask();
            getAsyncTask.setServerUrl(MainActivity.serverUrl+improveInfoUrl);
            getAsyncTask.setEmail(email);
            getAsyncTask.setPhone(phone);
            getAsyncTask.setUsername(user);
            getAsyncTask.setStudent_id(studentId);
            getAsyncTask.setOnDataFinishedListener(new GetAsyncTask.OnDataFinishedListener() {

                @Override
                public void onDataSuccessfully(Object s) {
                    try {
                        JSONObject jsonObject = new JSONObject((String) s);
                        String data = jsonObject.getString("data");
                        String msg = jsonObject.getString("msg");
                        Toast.makeText(ModifyPersonalInfo.this, msg, Toast.LENGTH_SHORT).show();//登录提示
                        if (data != null && data.equals("true")) {
                            if (data != null && data.equals("true")) {
                                //保存学号和密码
                                SharedPreferences.Editor editor = modify_sp.edit();
                                editor.putString("STUDENTID", studentId);
                                editor.putString("USERNAME", user);
                                editor.putString("PHONE", phone);
                                editor.putString("EMAIL",email);
                                editor.commit();
                                finish();
                            };
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDataFailed() {
                    Toast.makeText(ModifyPersonalInfo.this, "加载失败！", Toast.LENGTH_SHORT).show();
                }
            });
            getAsyncTask.execute();
        }
    }

    public boolean isEmailAndPhoneAndUserValid() {
        if (mEditEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.email_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mEditCal.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.call_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mEditUser.getText().toString().trim().equals("")){
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
