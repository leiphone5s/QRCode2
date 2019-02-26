package activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lei.qrcode.MainActivity;
import com.lei.qrcode.R;

import org.json.JSONObject;

import common.GetAsyncTask;

public class GetbackPwd extends AppCompatActivity {
    private Toolbar mGetBacktoolbar;
    private Button mGetBackPwd;
    private EditText mGetbackPwdEdit;
    //服务端地址
    final String getbackUrl = "/user/pwd";
    private SharedPreferences login_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getback_pwd);
        mGetBacktoolbar=findViewById(R.id.getbackpwd_toolbar);
        mGetBacktoolbar.setTitle("找回密码");

        mGetBackPwd=findViewById(R.id.getbackpwd_btn_sure);
        mGetBackPwd.setOnClickListener(mGetBackPwd_Listener);

        mGetbackPwdEdit=findViewById(R.id.getbackpwd_edit_email);
        login_sp = getSharedPreferences("userInfo", 0);
        setSupportActionBar(mGetBacktoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    View.OnClickListener mGetBackPwd_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.getbackpwd_btn_sure://确认按钮的监听事件
                    getBackPwd();
                    break;
            }
        }
    };

    public void getBackPwd() {                                //确认按钮的监听事件
        if (isUserEmailValid()) {
            Log.d("lei","getbackpwd is right");
            String emailAddress = mGetbackPwdEdit.getText().toString().trim();
            try {
                GetAsyncTask getbackAsyncTask = new GetAsyncTask();
                getbackAsyncTask.setEmail(emailAddress);
                getbackAsyncTask.setServerUrl(MainActivity.serverUrl+getbackUrl);
                getbackAsyncTask.setOnDataFinishedListener(new GetAsyncTask.OnDataFinishedListener() {

                    @Override
                    public void onDataSuccessfully(Object s) {
                        try {
                            JSONObject jsonObject = new JSONObject((String) s);
                            String data = jsonObject.getString("data");
                            String msg = jsonObject.getString("msg");
                            Toast.makeText(GetbackPwd.this, msg, Toast.LENGTH_SHORT).show();//登录提示
                            if (data != null && data.equals("true")) {
                                SharedPreferences.Editor editor = login_sp.edit();
                                editor.commit();
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDataFailed() {
                        Toast.makeText(GetbackPwd.this, "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                getbackAsyncTask.execute();
            } catch (Exception e) {

            }
        }
    }
    public boolean isUserEmailValid(){
        String emailAddress = mGetbackPwdEdit.getText().toString().trim();
        if(emailAddress.contains("@")){
            return true;
        }else if(emailAddress.equals("")){
            Toast.makeText(GetbackPwd.this,"邮箱地址不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            Toast.makeText(GetbackPwd.this,"邮箱地址输入不正确",Toast.LENGTH_SHORT).show();
            return false;
        }

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
