package com.itimetraveler.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapter.NumericWheelAdapter;
import view.AbsWheelView;
import view.TextWheelPicker;
import view.WheelView;


public class MainActivity extends AppCompatActivity {
    private TextView mTextView;
    private WheelView mWheelView;
    private NumericWheelAdapter adapter;

    private TextWheelPicker mTextWheelPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.hello_world);

//        adapter = new NumericWheelAdapter(this, 1, 15);
//        mWheelView = (WheelView) findViewById(R.id.wheel_picker);
//        mWheelView.setAdapter(adapter);
//        mWheelView.setSelectItem(2);
//
//        mWheelView.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AbsWheelView parentView, int index) {
//                mTextView.setText("" + index);
//            }
//        });


		List<String> mList = new ArrayList<String>();
//        for (int i = 0; i <= 200; i++){
//            mList.add("" + i + "月" + i + "日 周四");
//        }

        int daysCount = 100;
        mList = generateDateList(daysCount);
        mTextWheelPicker = (TextWheelPicker) findViewById(R.id.text_wheel_picker);
        mTextWheelPicker.setTextList(mList);
        mTextWheelPicker.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AbsWheelView parentView, int index) {
                mTextView.setText("" + index);
            }
        });
        mTextWheelPicker.setSelectItem(daysCount);
    }


    private List<String> generateDateList(int daysCount){
        Format dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Format weekFormat = new SimpleDateFormat("E");
        Date today = new Date();

        final int todayIdx = daysCount;
        String[] arr = new String[daysCount * 2 + 1];
        for(int i = daysCount; i > 0; i--){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, -i);// 今天-1天
            arr[todayIdx - i] = dateFormat.format(c.getTime()) + " " + weekFormat.format(c.getTime());

            Log.v("date c", "i:"+ i + ", "+ dateFormat.format(c.getTime()) + " " + weekFormat.format(c.getTime()));

            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            c1.add(Calendar.DAY_OF_MONTH, i);// 今天+1天
            arr[todayIdx + i] = dateFormat.format(c1.getTime()) + " " + weekFormat.format(c.getTime());

            Log.v("date c1", "i:"+ i + ", "+ dateFormat.format(c1.getTime()) + " " + weekFormat.format(c.getTime()));
        }
        arr[todayIdx] = dateFormat.format(today);

        Log.v("date", ""+ Arrays.toString(arr));
        Log.v("date today", ""+ dateFormat.format(today));

        return Arrays.asList(arr);
    }
}
