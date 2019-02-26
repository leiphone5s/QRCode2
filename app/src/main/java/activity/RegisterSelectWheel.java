package activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lei.qrcode.R;

import widget.BaseWheelView;
import widget.OnWheelChangedListener;
import widget.WheelView;
import widget.adapters.ArrayWheelAdapter;

public class RegisterSelectWheel extends BaseWheelView implements View.OnClickListener, OnWheelChangedListener {

    private WheelView mViewFaculty;
    private WheelView mViewDepart;
    private WheelView mViewClass;
    private TextView mBtnConfirm,mBtnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerselectwheel);
        setUpViews();
        setUpListener();
        setUpData();
    }

    private void setUpViews() {
        mViewFaculty = findViewById(R.id.id_province);
        mViewDepart = findViewById(R.id.id_city);
        mViewClass = findViewById(R.id.id_district);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnCancel=findViewById(R.id.btn_cancel);
    }

    private void setUpListener() {
        // 添加change事件
        mViewFaculty.addChangingListener(this);
        // 添加change事件
        mViewDepart.addChangingListener(this);
        // 添加change事件
        mViewClass.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);

        mBtnCancel.setOnClickListener(this);
    }

    private void setUpData() {
        initFacultyDatas();
        mViewFaculty.setViewAdapter(new ArrayWheelAdapter<String>(RegisterSelectWheel.this, mFacultyDatas));
        // 设置可见条目数量
        mViewFaculty.setVisibleItems(7);
        mViewDepart.setVisibleItems(7);
        mViewClass.setVisibleItems(7);
        updateDepart();
        updateClass();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewFaculty) {
            updateDepart();
        } else if (wheel == mViewDepart) {
            updateClass();
        } else if (wheel == mViewClass) {
            mCurrentClassName = mClassDatasMap.get(mCurrentDepartName)[newValue];
            mCurrentClassCode = mClasscodeDatasMap.get(mCurrentClassName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateClass() {
        int pCurrent = mViewDepart.getCurrentItem();
        mCurrentDepartName = mDepartDatasMap.get(mCurrentFacultyName)[pCurrent];
        Log.d("feng","mCurrentDepartName = "+mCurrentDepartName+"mCurrentClassName  = "+mCurrentClassName);
        String[] classes = mClassDatasMap.get(mCurrentDepartName);


        if (classes == null) {
            Log.d("feng","areas == null");
            classes = new String[] { "上海","南京" };
        }
        mViewClass.setViewAdapter(new ArrayWheelAdapter<String>(this, classes));
        mViewClass.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateDepart() {
        int pCurrent = mViewFaculty.getCurrentItem();
        mCurrentFacultyName = mFacultyDatas[pCurrent];
        String[] departs = mDepartDatasMap.get(mCurrentFacultyName);
        if (departs == null) {
            departs = new String[] { "" };
        }
        mViewDepart.setViewAdapter(new ArrayWheelAdapter<String>(this, departs));
        mViewDepart.setCurrentItem(0);
        updateClass();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                showSelectedResult();
                finish();
                break;
            case R.id.btn_cancel:
                finish();
            default:
                break;
        }
    }

    private void showSelectedResult() {
        Intent intent = new Intent();
        // 获取用户计算后的结果
        intent.putExtra("faculty", mCurrentFacultyName); //将计算的值回传回去
        intent.putExtra("depart", mDepartDatasMap.get(mCurrentFacultyName)[mViewDepart.getCurrentItem()]);
        intent.putExtra("class", mClassDatasMap.get(mCurrentDepartName)[mViewClass.getCurrentItem()]);
        //通过intent对象返回结果，必须要调用一个setResult方法，
        //setResult(resultCode, data);第一个参数表示结果返回码，一般只要大于1就可以，但是
        setResult(2, intent);
    }
}
