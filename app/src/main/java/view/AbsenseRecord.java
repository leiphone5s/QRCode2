package view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
import com.google.gson.JsonObject;
import com.lei.qrcode.MainActivity;
import com.lei.qrcode.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import adapter.DPCManager;
import adapter.DPDecor;
import adapter.DPMode;
import adapter.DatePicker;
import adapter.DatePicker2;
import common.DateData;
import common.GetAsyncTask;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.MyTableTextView;


public class AbsenseRecord extends AppCompatActivity{

    DatePicker myDatepicker;

    private Context mContext;

    private DPCManager dpcManager;

    private String recordUrl = "/user/signlist";
    private SharedPreferences login_sp;
    private final int REFRESH = 123;

    private LinearLayout mainLinerLayout;
    private RelativeLayout relativeLayout;
    MyTableTextView txt1,txt2,txt3,txt4,txt5;
    private String[] name={"课程名称","教学老师","上课地点","签到时间","签到状态"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absense_record);

        mContext = this;
        mainLinerLayout = (LinearLayout) this.findViewById(R.id.MyTable);

        initData();

        Calendar calendar = Calendar.getInstance(); //获取系统的日期
        // 年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH)+1;


        // 自定义背景绘制示例 Example of custom date's background
        List<String> tmp = new ArrayList<>();
        tmp.add("2019-1-1");
        tmp.add("2019-1-8");
        tmp.add("2019-1-16");
        tmp.add("2019-2-1");
        tmp.add("2019-3-8");
        tmp.add("2019-5-16");
        tmp.add("2019-6-1");
        tmp.add("2019-7-8");
        tmp.add("2019-3-16");
        tmp.add("2019-3-15");
        tmp.add("2019-4-8");
        tmp.add("2019-4-16");
        tmp.add("2019-5-13");
        tmp.add("2019-6-28");
        tmp.add("2019-7-16");
        tmp.add("2018-4-8");
        tmp.add("2018-11-16");
        tmp.add("2018-12-13");
        tmp.add("2018-12-28");
        tmp.add("2018-12-16");
        DPCManager.getInstance().setDecorBG(tmp);

        DatePicker2 picker = (DatePicker2) findViewById(R.id.main_dp);
        picker.setDate(year, month);
        picker.setMode(DPMode.SINGLE);
        picker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(getResources().getColor(R.color.qingse));
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.1F, paint);
            }
        });
        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                searchAbsenseRecord(date);
                Toast.makeText(AbsenseRecord.this, date, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //绑定数据
    private void initData() {

        //初始化标题
        relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
        MyTableTextView title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_1);
        title.setText(name[0]);
        title.setTextSize(15);
        title.setTextColor(Color.BLUE);

        title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_2);
        title.setText(name[1]);
        title.setTextSize(15);
        title.setTextColor(Color.BLUE);
        title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_3);
        title.setTextSize(15);
        title.setText(name[2]);
        title.setTextColor(Color.BLUE);
        title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_4);
        title.setTextSize(15);
        title.setText(name[3]);
        title.setTextColor(Color.BLUE);
        title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_5);
        title.setTextSize(15);
        title.setText(name[4]);
        title.setTextColor(Color.BLUE);
        mainLinerLayout.addView(relativeLayout);

        //初始化内容
        int number = 1;
        for (int i=0;i<4;i++) {
            relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
            txt1 = relativeLayout.findViewById(R.id.list_1_1);
            txt1.setText("计算机应用基础（上机）");
            txt2 = relativeLayout.findViewById(R.id.list_1_2);
            txt2.setText("左莉");
            txt3 = relativeLayout.findViewById(R.id.list_1_3);
            txt3.setText("E401");
            txt4 = relativeLayout.findViewById(R.id.list_1_4);
            txt4.setText("17:01");
            txt5 = relativeLayout.findViewById(R.id.list_1_5);
            txt5.setText("已签到");
            mainLinerLayout.addView(relativeLayout);
            number++;
        }
    }


    private void searchAbsenseRecord(String date){
        SharedPreferences pref = getSharedPreferences("userInfo",MODE_PRIVATE);//返回1说明用户名和密码均正确
        //保存学号和密码
        String studentId = pref.getString("STUDENTID","");
        Log.d("lei","date = "+date);
        RecordAsyncTask recordAsyncTask = new RecordAsyncTask(MainActivity.serverUrl+recordUrl+
                "?pageSize=10&page=0&student_no="+studentId+"&start_time="+ date);
        recordAsyncTask.execute();
    }

    private class RecordAsyncTask extends AsyncTask<Void, Void, String> {

        private String serverUrl;
        String responseData = null;

        public RecordAsyncTask(String url) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseData;
        }

        @Override
        protected void onPostExecute(String s) {

            //服务器返回的数据
            Log.d("lei","s = "+s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                String class_name = jsonObject1.getString("class_name");
                String place = jsonObject1.getString("place");
                String teacher_name = jsonObject1.getString("teacher_name");
                String update_time = jsonObject1.getString("update_time");
//                String msg = jsonObject.getString("msg");
//                String data = jsonObject.getString("data");
                //Log.d("lei","code = "+code+"  msg = "+msg+"  data = "+data);
                //Toast.makeText(AbsenseRecord.this, msg, Toast.LENGTH_SHORT).show();
                //Log.d("lei","msg = "+msg+"   isFinish = "+(data.equals("true")));
                String[] result = new String[5];
                if(class_name != null && teacher_name != null && place != null && update_time != null) {
                    result[0] = class_name;
                    result[1] = teacher_name;
                    result[2] = place;
                    result[3] = update_time;
                    result[4] = "已签到";
                }
                Message message = new Message();
                message.obj = result;
                message.what = REFRESH;
                myHandler.sendMessage(message);

            }catch(Exception e){}
            super.onPostExecute(s);
        }

    }
    // 实例化一个handler
    Handler myHandler = new Handler() {
        //接收到消息后处理
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                    String[] datas = (String[])msg.obj;
                    txt1.setText(datas[0]);
                    txt2.setText(datas[1]);
                    txt3.setText(datas[2]);
                    txt4.setText(datas[3]);
                    txt5.setText(datas[4]);
                    break;
            }
            super.handleMessage(msg);
        }
    };



}
