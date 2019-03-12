package activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import calendar.AdvanceTime;
import calendar.CalendarEvent;
import calendar.CalendarProviderManager;
import utils.TimeTransform;

import com.lei.qrcode.R;

import java.util.List;

public class AbsenseWarn extends AppCompatActivity {

    @BindView(R.id.course_tip)
    CheckBox tipCource;
    @BindView(R.id.ring_tip)
    CheckBox tipRing;
    @BindView(R.id.shake_tip)
    CheckBox shakeRing;
    @BindView(R.id.spinner_tip)
    Spinner tipSpinner;

    private int[] timeArrays = {AdvanceTime.FIFTH_MINUTES,AdvanceTime.THIRTY_MINUTES,AdvanceTime.ONE_HOUR};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absense_warn);
        ButterKnife.bind(this);
        tipSpinner.setOnItemSelectedListener(new spinnerListener());
    }

        @OnClick({R.id.course_tip, R.id.ring_tip,
            R.id.shake_tip})
        public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.course_tip:

                break;
            case R.id.ring_tip:

                break;
            case R.id.shake_tip:

                break;
            default:
                break;
        }
    }

    class spinnerListener implements android.widget.AdapterView.OnItemSelectedListener{


        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            //将选择的元素显示出来
                //String selected = parent.getItemAtPosition(position).toString();
            long calID = CalendarProviderManager.obtainCalendarAccountID(AbsenseWarn.this);
            List<CalendarEvent> events = CalendarProviderManager.queryAccountEvent(AbsenseWarn.this, calID);

                int result = CalendarProviderManager.addCalendarEvent(AbsenseWarn.this, "计算机基础",
                        "课程提醒", "E1107", TimeTransform.timeStrToSecond("2019-03-08 17:30:00"),//System.currentTimeMillis(),
                        TimeTransform.timeStrToSecond("2019-03-08 18:30:00"), timeArrays[position], null);
                    CalendarProviderManager.addCalendarEvent(AbsenseWarn.this, "计算机基础",
                    "课程提醒", "E1107", TimeTransform.timeStrToSecond("2019-03-09 17:30:00"),//System.currentTimeMillis(),
                    TimeTransform.timeStrToSecond("2019-03-09 18:30:00"), timeArrays[position], null);
               // Log.d("lei",position+"");
                if (result == 0) {
                    //Toast.makeText(AbsenseWarn.this, "插入成功", Toast.LENGTH_SHORT).show();
                } else if (result == -1) {
                    //Toast.makeText(AbsenseWarn.this, "插入失败", Toast.LENGTH_SHORT).show();
                } else if (result == -2) {
                    //Toast.makeText(AbsenseWarn.this, "没有权限", Toast.LENGTH_SHORT).show();
                }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            System.out.println("nothingSelect");
        }
    }


//    @OnClick({R.id.btn_main_add, R.id.btn_main_delete,
//            R.id.btn_main_update, R.id.btn_main_query})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.btn_main_add:
//                // 添加事件
//                Log.d("lei",System.currentTimeMillis()+"");
//                int result = CalendarProviderManager.addCalendarEvent(this, "计算机基础",
//                        "课程提醒", "E1107", System.currentTimeMillis(),
//                        System.currentTimeMillis() + 60000, 0, null);
//                if (result == 0) {
//                    Toast.makeText(this, "插入成功", Toast.LENGTH_SHORT).show();
//                } else if (result == -1) {
//                    Toast.makeText(this, "插入失败", Toast.LENGTH_SHORT).show();
//                } else if (result == -2) {
//                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.btn_main_delete:
//                // 删除事件
//                long calID2 = CalendarProviderManager.obtainCalendarAccountID(this);
//                List<CalendarEvent> events2 = CalendarProviderManager.queryAccountEvent(this, calID2);
//                if (null != events2) {
//                    if (events2.size() == 0) {
//                        Toast.makeText(this, "没有事件可以删除", Toast.LENGTH_SHORT).show();
//                    } else {
//                        long eventID = events2.get(0).getId();
//                        int result2 = CalendarProviderManager.deleteCalendarEvent(this, eventID);
//                        if (result2 == -2) {
//                            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.btn_main_update:
//                // 更新事件
//                long calID = CalendarProviderManager.obtainCalendarAccountID(this);
//                List<CalendarEvent> events = CalendarProviderManager.queryAccountEvent(this, calID);
//                if (null != events) {
//                    if (events.size() == 0) {
//                        Toast.makeText(this, "没有事件可以更新", Toast.LENGTH_SHORT).show();
//                    } else {
//                        long eventID = events.get(0).getId();
//                        int result3 = CalendarProviderManager.updateCalendarEventTitle(
//                                this, eventID, "改吃晚饭的房间第三方监督司法");
//                        if (result3 == 1) {
//                            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.btn_main_query:
//                // 查询事件
//                long calID4 = CalendarProviderManager.obtainCalendarAccountID(this);
//                List<CalendarEvent> events4 = CalendarProviderManager.queryAccountEvent(this, calID4);
//                StringBuilder stringBuilder4 = new StringBuilder();
//                if (null != events4) {
//                    for (CalendarEvent event : events4) {
//                        stringBuilder4.append(events4.toString()).append("\n");
//                    }
//                    tvEvent.setText(stringBuilder4.toString());
//                    Toast.makeText(this, "查询成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                break;
//        }
//    }

}
