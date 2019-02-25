package common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetAsyncTask extends AsyncTask<Void, Void, String> {

    //帐号
    private String username;
    //密码
    private String pwd;
    //学号
    private String student_id;
    //性别
    private String sexuality;

    private String serverUrl;

    private String phone;

    private String email;

    private String oldPwd;

    private String newPwd;

    private String year;

    private String month;

    private String day;

    private String page;

    private String pageSize;

    private String startTime;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getOldPwd() {

        return oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    String responseData = null;

    OnDataFinishedListener onDataFinishedListener;

    public GetAsyncTask() {
    }

    public GetAsyncTask(String url) {
        this.serverUrl=url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public void setSexuality(String sexuality) {
        this.sexuality = sexuality;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {

        return username;
    }

    public String getPwd() {
        return pwd;
    }

    public String getStudent_id() {
        return student_id;
    }

    public String getSexuality() {
        return sexuality;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setOnDataFinishedListener(
            OnDataFinishedListener onDataFinishedListener) {
        this.onDataFinishedListener = onDataFinishedListener;
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


        FormBody.Builder s=new FormBody.Builder();
        if(student_id!=null){
            s.add("student_no", student_id);
        }
        if(username!=null){
            s.add("username",username);
        }
        if(pwd!=null){
            s.add("password",pwd);
        }
        if(email!=null){
            s.add("email",email);
        }
        if(phone!=null){
            s.add("phone",phone);
        }
        if(newPwd!=null){
            s.add("new_password",newPwd);
        }
        if(oldPwd!=null){
            s.add("old_password",oldPwd);
        }
        if(sexuality!=null){
            s.add("sex",sexuality);
        }
        if(page != null){
            s.add("page",page);
        }
        if(pageSize != null){
            s.add("pageSize",pageSize);
        }
        if(startTime != null){
            s.add("start_time",startTime);
        }

        RequestBody formBody = s.build();
        //建立请求表单，添加上传服务器的参数
//        RequestBody formBody = new FormBody.Builder()
//                .add("student_no", student_id)
//                .add("username",username)
//                .add("password",pwd)
//                .add("email",email)
//                .add("phone",phone)
//                .build();

        //发起请求
        Request request = new Request.Builder()
                .url(serverUrl)
                .post(formBody)
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
            responseData = response.body().string();
            Log.d("lei","responseData ="+responseData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    @Override
    protected void onPostExecute(String s) {

        Log.d("lei","s ="+s);
        //服务器返回的数据 s
        if(s!=null){
            onDataFinishedListener.onDataSuccessfully(s);
        }else{
            onDataFinishedListener.onDataFailed();
        }

        super.onPostExecute(s);
    }

    //回调接口：
    public interface OnDataFinishedListener {

        public void onDataSuccessfully(Object data);

        public void onDataFailed();

    }
}


