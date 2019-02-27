package com.lei.qrcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import activity.AbsenseRecord;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import activity.PersonalSettings;
import utils.ACache;
import utils.Globals;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button scanBtn;
    private TextView qrcode;
    private EditText contentEt;
    private Button encodeBtn;
    private ImageView contentIv;
    private Toolbar toolbar;
    private int REQUEST_CODE_SCAN = 111;
    private String recordUrl = "/user/signlist";
    public static String serverUrl = "http://192.168.137.215:7001";
    //public static String serverUrl = "http://192.168.0.119:7001";
    //public static String serverUrl = "http://47.105.138.71:80";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ImageView menu;

    /**
     * 生成带logo的二维码
     */
    private Button encodeBtnWithLogo;
    private ImageView contentIvWithLogo;
    private String contentEtString;
    private long firstTime = 0;
    SharedPreferences main_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    protected void initTodayRecord() {
        String studentId = main_pref.getString("STUDENTID","");
        Calendar calendar = Calendar.getInstance(); //获取系统的日期
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        GetQrAsyncTask getQrAsyncTask1 = new GetQrAsyncTask(MainActivity.serverUrl+recordUrl+
                "?pageSize=10&page=0&student_no="+studentId+"&start_time="+ (year+"-"+month+"-"+day));
        getQrAsyncTask1.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTodayRecord();
    }

    private void initView() {
        main_pref = getSharedPreferences("userInfo",MODE_PRIVATE);
        String studentId = main_pref.getString("STUDENTID","");
        boolean isTeacher = studentId.contains("6");
        /*扫描按钮*/
        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);
        /*扫描结果*/
        qrcode = findViewById(R.id.Qrcode);

        /*要生成二维码的输入框*/
        contentEt = findViewById(R.id.contentEt);
        /*生成按钮*/
        encodeBtn = findViewById(R.id.encodeBtn);
        encodeBtn.setOnClickListener(this);
        /*生成的图片*/
        contentIv = findViewById(R.id.contentIv);
        if(isTeacher)
        {
            scanBtn.setVisibility(View.GONE);
            qrcode.setText("一键签到");
            qrcode.setOnClickListener(MySignListener);
        }
//        toolbar = findViewById(R.id.toolbar);
//
//
//        toolbar.setTitle("扫一扫");
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_na);
        navigationView = (NavigationView) findViewById(R.id.nav);
        menu= (ImageView) findViewById(R.id.main_menu);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        menu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) { //item.setChecked(true);
                switch (item.getItemId()) {

                    case R.id.personsettings:
                        Intent i = new Intent(MainActivity.this, PersonalSettings.class) ;    //切换Login Activity至User Activity
                        startActivity(i);
                        break;
                    case R.id.aboutus:
                        Intent intentToAboutUs = new Intent(MainActivity.this, activity.AboutUs.class) ;    //切换Login Activity至User Activity
                        startActivity(intentToAboutUs);
                        break;
                    case R.id.absenserecord:
                        Intent intentToAbsenseRecord = new Intent(MainActivity.this, activity.AbsenseRecord.class) ;    //切换Login Activity至User Activity
                        startActivity(intentToAbsenseRecord);
                        break;
                    case R.id.absensewarn:
                        Intent intentToAbsenseWarn = new Intent(MainActivity.this, activity.AbsenseWarn.class) ;    //切换Login Activity至User Activity
                        startActivity(intentToAbsenseWarn);
                        break;
                    case R.id.absenseconstant:
                        Intent intentToAbsenseConstant = new Intent(MainActivity.this, activity.AbsenseConstantActivity.class) ;    //切换Login Activity至User Activity
                        startActivity(intentToAbsenseConstant);
                        break;
                }

                //Toast.makeText(MainActivity.this,item.getTitle().toString(),Toast.LENGTH_SHORT).show();
                //drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {

        Bitmap bitmap = null;
        switch (v.getId()) {
            case R.id.scanBtn:

                AndPermission.with(this)
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                                /*ZxingConfig是配置类
                                 *可以设置是否显示底部布局，闪光灯，相册，
                                 * 是否播放提示音  震动
                                 * 设置扫描框颜色等
                                 * 也可以不传这个参数
                                 * */
//                                ZxingConfig config = new ZxingConfig();
//                                config.setPlayBeep(false);//是否播放扫描声音 默认为true
//                                config.setShake(false);//是否震动  默认为true
//                                config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
//                                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
//                                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
//                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();

                break;
            case R.id.encodeBtn:
                contentEtString = contentEt.getText().toString().trim();
                if (TextUtils.isEmpty(contentEtString)) {
                    Toast.makeText(this, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, null);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    contentIv.setImageBitmap(bitmap);
                }

                break;

            case R.id.encodeBtnWithLogo:

                contentEtString = contentEt.getText().toString().trim();
                if (TextUtils.isEmpty(contentEtString)) {
                    Toast.makeText(this, "请输入要生成二维码图片的字符串", Toast.LENGTH_SHORT).show();
                    return;
                }

                bitmap = null;
                try {
                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    contentIvWithLogo.setImageBitmap(bitmap);
                }

                break;
            case R.id.main_menu://点击菜单，跳出侧滑菜单
                if (drawerLayout.isDrawerOpen(navigationView)){
                    drawerLayout.closeDrawer(navigationView);
                }else{
                    drawerLayout.openDrawer(navigationView);
                }
                break;
            default:
        }
    }


    // 或者,这里是创建一个OnClickListener 的对象，与上面的直接复写接口有异曲同工之妙
    View.OnClickListener MySignListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this,"签到成功～",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String studentid = main_pref.getString("STUDENTID","eq"); //获取当前输入的用户名和密码信息
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                GetQrAsyncTask getQrAsyncTask = new GetQrAsyncTask(MainActivity.serverUrl+content+"?student_no="+studentid+"&class_no=1");
                getQrAsyncTask.execute();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //新建xml文件
        //setIconVisible(menu,true);
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private class GetQrAsyncTask extends AsyncTask<Void, Void, String> {

        private String serverUrl;
        String responseData = null;

        public GetQrAsyncTask(String url) {
            serverUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            Response response = null;
            final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                            cookieStore.put(httpUrl.host(), list);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                            List<Cookie> cookies = cookieStore.get(httpUrl.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })
                    .build();

            //发起请求
            Request request = new Request.Builder()
                    .url(serverUrl)
                    .build();
            try {
                response = okHttpClient.newCall(request).execute();
                responseData = response.body().string();
                Log.d("lei", "responseData =" + responseData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseData;
        }

        @Override
        protected void onPostExecute(String s) {

            //服务器返回的数据
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getJSONArray("list")!=null && jsonObject.getJSONArray("list").length() != 0){
                    ACache.get(MainActivity.this).remove(Globals.DAYRECORD);
                    ACache.get(MainActivity.this).put(Globals.DAYRECORD,s);
                }
                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");
                String data = jsonObject.getString("data");
                Log.d("lei","code = "+code+"  msg = "+msg+"  data = "+data);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();//登录成功提示
                //Log.d("lei","msg = "+msg+"   isFinish = "+(data.equals("true")));
                //Toast.makeText(LoginActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();//登录成功提示

            }catch(Exception e){}
            super.onPostExecute(s);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ( secondTime - firstTime < 2000) {
                //System.exit(0);
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                return true;
            } else {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
