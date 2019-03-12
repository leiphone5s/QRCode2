package activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import adapter.TableAdapter;
import chart.StudyReportPieChart;
import common.GetAsyncTask;
import model.AbsenseInfo;
import model.KnowledgeMasteryReport;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.ACache;
import utils.Globals;


import com.google.gson.JsonObject;
import com.lei.qrcode.LoginActivity;
import com.lei.qrcode.MainActivity;
import com.lei.qrcode.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by ldf on 17/7/13.
 */

public class AbsenseConstantActivity extends AppCompatActivity {

    //private  final int REFRESH_PIC = 10;
    StudyReportPieChart pieChart;
    private static final int TIMER = 999;
    private static boolean flag = true;
    KnowledgeMasteryReport report;
    TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absense_constant);
        pieChart = findViewById(R.id.pie_chart);
        mTextView = findViewById(R.id.sign_number2);
        report = new KnowledgeMasteryReport();
        report.setKnowKnowledgeCount("0");
        report.setWeakKnowledgeCount("1");
        pieChart.notifyDataChanged(report);
        flag = true;
        setTimer();

    }

    private void intiGetSignDataFromAliyun(){
        try{
            GetSignAsyncTask getSignAsyncTask = new GetSignAsyncTask(MainActivity.serverUrl+"/sign/getSignData?lesson_id=1");
            getSignAsyncTask.setOnDataFinishedListener(new AbsenseConstantActivity.OnDataFinishedListener() {

                @Override
                public void onDataSuccessfully(Object s) {
                    try {
                        JSONObject jsonObject = new JSONObject((String)s);
                        JSONObject jsonObject1 = (JSONObject) jsonObject.get("data");
                        String total = jsonObject1.getString("total");
                        String actual_num = jsonObject1.getString("actual_num");
                        Log.d("lei",total+"***********"+actual_num);
                        if(total != null && actual_num !=null){
                            ACache.get(AbsenseConstantActivity.this).put(Globals.TOTAL,total);
                            ACache.get(AbsenseConstantActivity.this).put(Globals.ACTUAL_NUM,actual_num);
                            mHandler.sendEmptyMessage(TIMER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onDataFailed() {
                    Toast.makeText(AbsenseConstantActivity.this, "获取失败！", Toast.LENGTH_SHORT).show();
                }
            });
            getSignAsyncTask.execute();
        }catch(Exception e){

        }
    }

    private void setTimer(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag){
                    try {
                        intiGetSignDataFromAliyun();
                        Thread.sleep(2000); //休眠一秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    // 实例化一个handler
    Handler mHandler = new Handler() {
        //接收到消息后处理
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIMER:
                    report.setWeakKnowledgeCount(ACache.get(AbsenseConstantActivity.this).getAsString(Globals.TOTAL) );
                    report.setKnowKnowledgeCount(ACache.get(AbsenseConstantActivity.this).getAsString(Globals.ACTUAL_NUM));
                    mTextView.setText(ACache.get(AbsenseConstantActivity.this).getAsString(Globals.ACTUAL_NUM)+"/"+ACache.get(AbsenseConstantActivity.this).getAsString(Globals.TOTAL));
                    Log.d("lei",ACache.get(AbsenseConstantActivity.this).getAsString(Globals.TOTAL)+"++++++"+ACache.get(AbsenseConstantActivity.this).getAsString(Globals.ACTUAL_NUM));
                    pieChart.notifyDataChanged(report);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void stopTimer(){
        flag = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private class GetSignAsyncTask extends AsyncTask<Void, Void, String> {

        private String serverUrl;
        String responseData = null;
        OnDataFinishedListener onDataFinishedListener;

        public void setOnDataFinishedListener(OnDataFinishedListener onDataFinishedListener) {
            this.onDataFinishedListener = onDataFinishedListener;
        }

        public GetSignAsyncTask(String url) {
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

            if(s!=null){
                onDataFinishedListener.onDataSuccessfully(s);
            }else{
                onDataFinishedListener.onDataFailed();
            }

//            //服务器返回的数据
//            try {
//                JSONObject jsonObject = new JSONObject(s);
//
//                String code = jsonObject.getString("code");
//                String msg = jsonObject.getString("msg");
//                String data = jsonObject.getString("data");
//                Log.d("lei","code = "+code+"  msg = "+msg+"  data = "+data);
//                Toast.makeText(AbsenseConstantActivity.this,msg,Toast.LENGTH_SHORT).show();
//                //Utils.showShortToast(MainActivity.this,msg);
//            }catch(Exception e){}
            super.onPostExecute(s);
        }

    }

    //回调接口：
    public interface OnDataFinishedListener {

        public void onDataSuccessfully(Object data);

        public void onDataFailed();

    }


}
