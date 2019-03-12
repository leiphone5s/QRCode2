package activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lei.qrcode.LoginActivity;
import com.lei.qrcode.R;

import java.io.File;

import common.Utils;
import utils.ACache;
import utils.Globals;

public class PersonalSettings extends AppCompatActivity implements View.OnClickListener{

    private Button btnExit;
    private TextView settings_remind,settings_security,settings_modifypwd,settings_about,settings_info;
    //private LinearLayout mSettingsPersonalInfo,mSettingsSecurity,mSettingsModifyPwd,mSettingsRemind,mSettingsLogout;
    private SharedPreferences login_sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        initView();
    }

    private void initView(){
        btnExit=findViewById(R.id.btnexit);
        settings_remind=findViewById(R.id.txt_msgtip);
        settings_security=findViewById(R.id.txt_usersafe);
        settings_modifypwd=findViewById(R.id.txt_modifypwd);
        settings_about=findViewById(R.id.txt_about);
        settings_info = findViewById(R.id.txt_personinfo);

        btnExit.setOnClickListener(this);
        settings_remind.setOnClickListener(this);
        settings_security.setOnClickListener(this);
        settings_modifypwd.setOnClickListener(this);
        settings_about.setOnClickListener(this);
        settings_info.setOnClickListener(this);

        login_sp = getSharedPreferences("userInfo", 0);
    }


    @Override
    public void onClick(View v) {

        Bitmap bitmap = null;
        switch (v.getId()) {
            case R.id.btnexit:
                showLogoutDialog();
                break;
            case R.id.txt_modifypwd:
                Utils.start_Activity(PersonalSettings.this, Resetpwd.class);
                break;
            case R.id.txt_personinfo:
                Utils.start_Activity(PersonalSettings.this, ModifyPersonalInfo.class);
                break;
            case R.id.txt_msgtip:
                Toast.makeText(this, "考勤提醒", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txt_usersafe:
                Toast.makeText(this, "安全设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txt_about:
                Toast.makeText(this, "关于扫一扫", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }

    private void showLogoutDialog(){
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSettings.this);
        //    设置Title的图标
        builder.setIcon(R.mipmap.backgroud);
        //    设置Title的内容
        builder.setTitle("账号注销");
        //    设置Content来显示一个信息
        builder.setMessage("确定注销该账号吗？");
        //    设置一个PositiveButton
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                logout();
                //Toast.makeText(PersonalSettings.this, "positive: " + which, Toast.LENGTH_SHORT).show();
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
               // Toast.makeText(PersonalSettings.this, "negative: " + which, Toast.LENGTH_SHORT).show();
            }
        });
        //    显示出该对话框
        builder.show();

    }

        private void logout () {           //注销
            SharedPreferences.Editor editor = login_sp.edit();
            editor.clear();
            editor.commit();
            ACache.get(PersonalSettings.this).remove(Globals.DAYRECORD);
            File[] files = new File("/data/data/" + this.getPackageName() + "/shared_prefs").listFiles();
            deleteCache(files);
            Intent intent = new Intent(PersonalSettings.this, LoginActivity.class);    //切换Login Activity至User Activity
            startActivity(intent);
            finish();
            //Toast.makeText(this, getString(R.string.cancel_success),Toast.LENGTH_SHORT).show();
        }

        private void deleteCache (File[]files){
            boolean flag;
            for (File itemFile : files) {
                flag = itemFile.delete();
                if (flag == false) {
                    deleteCache(itemFile.listFiles());
                }
            }
        }

}
