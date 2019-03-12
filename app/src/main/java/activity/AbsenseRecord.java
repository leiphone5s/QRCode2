package activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


//import com.google.gson.JsonObject;
//import com.google.gson.JsonObject;
import com.lei.qrcode.MainActivity;
import com.lei.qrcode.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import adapter.DPCManager;
import adapter.DPDecor;
import adapter.DPMode;
import adapter.DatePicker;
import adapter.DatePicker2;
import adapter.TableAdapter;
import model.AbsenseInfo;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.ACache;
import utils.Globals;


public class AbsenseRecord extends AppCompatActivity {

    DatePicker myDatepicker;

    private Context mContext;

    private DPCManager dpcManager;

    private String recordUrl = "/user/signlist";
    private SharedPreferences login_sp;
    private final int REFRESH = 123;
    private final int EMPTYABSENSE=456;

    private LinearLayout absense_list;
    private RelativeLayout relativeLayout;
    private TextView text_courseName, text_teacherName, text_place, text_signInTime, text_signType,text_emptyview;
    //private String[] name = {"课程名称", "教学老师", "上课地点", "签到时间", "签到状态"};
    ListView tableListView;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absense_record);
        mContext = this;
        initData();

        Calendar calendar = Calendar.getInstance(); //获取系统的日期
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;


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

        DatePicker2 picker = findViewById(R.id.main_dp);
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


//    //绑定数据
    private void initData() {
        pref = getSharedPreferences("userInfo",MODE_PRIVATE);//返回1说明用户名和密码均正确

        absense_list = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_item,null);//this.findViewById(R.id.absense_list);
        text_courseName = absense_list.findViewById(R.id.text_courseName);
        text_place = absense_list.findViewById(R.id.text_place);
        text_teacherName = absense_list.findViewById(R.id.text_teacherName);
        text_signInTime = absense_list.findViewById(R.id.text_signInTime);
        text_signType = absense_list.findViewById(R.id.text_signType);
        tableListView = findViewById(R.id.list);
       // text_emptyview = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.empty_list_view,null);
        text_emptyview = findViewById(R.id.text_tip);
        //text_emptyview.setVisibility(View.INVISIBLE);
        ViewGroup tableTitle = findViewById(R.id.table_title);
        tableTitle.setBackgroundColor(Color.rgb(177, 173, 172));
        initTodayAbsenseInfo();
    }

    protected void initTodayAbsenseInfo() {
        String studentId = pref.getString("STUDENTID","");
        Calendar calendar = Calendar.getInstance(); //获取系统的日期
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        RecordAsyncTask getQrAsyncTask1 = new RecordAsyncTask(MainActivity.serverUrl+recordUrl+
                "?pageSize=10&page=0&student_no="+studentId+"&start_time="+ (year+"-"+month+"-"+day));
        getQrAsyncTask1.execute();
    }
    private void searchAbsenseRecord(String date){
        String studentId = pref.getString("STUDENTID","");
        Log.d("lei","date = "+date);
        RecordAsyncTask recordAsyncTask = new RecordAsyncTask(MainActivity.serverUrl+recordUrl+
                "?pageSize=10&page=0&student_no="+studentId+"&start_time="+ date);
        recordAsyncTask.execute();
    }

    public class RecordAsyncTask extends AsyncTask<Void, Void, String> {

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
                    List<AbsenseInfo> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                            String class_name = jsonObject1.getString("lesson_name");
                            String place = jsonObject1.getString("place");
                            String teacher_name = jsonObject1.getString("teacher_name");
                            String update_time = jsonObject1.getString("update_time");
                            String sign_type = jsonObject1.getString("sign_type")+"已签到";
                            AbsenseInfo absenseInfo = new AbsenseInfo(class_name, teacher_name, place, update_time, sign_type);
                            list.add(absenseInfo);
                    }
                    Message message = new Message();
                    message.obj = list;
                    message.what = REFRESH;
                    myHandler.sendMessage(message);
                    Log.d("lei","send refresh message");
                } catch (Exception e) {
                }
            super.onPostExecute(s);
        }

    }
    // 实例化一个handler
    Handler myHandler = new Handler() {
        //接收到消息后处理
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                      Log.d("lei","receive refresh message");
                      List<AbsenseInfo> list  = (List<AbsenseInfo>)msg.obj;
                      if(list.size() != 0) {
                          text_emptyview.setVisibility(View.GONE);
                          TableAdapter adapter = new TableAdapter(AbsenseRecord.this, list);
                          tableListView.setAdapter(adapter);
                          tableListView.setEmptyView(text_emptyview);
                      }else{
                          text_emptyview.setVisibility(View.VISIBLE);
                          TableAdapter adapter = new TableAdapter(AbsenseRecord.this, list);
                          tableListView.setAdapter(adapter);
                          tableListView.setEmptyView(text_emptyview);
                      }
                    break;
            }
            super.handleMessage(msg);
        }
    };



}
