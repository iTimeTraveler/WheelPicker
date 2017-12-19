package com.itimetraveler.widget.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

import adapter.NumericWheelAdapter;
import view.AbsWheelView;
import view.TextWheelPicker;
import view.WheelView;


public class MainActivity extends AppCompatActivity {
    TextView mTextView;
    WheelView mWheelView;
    NumericWheelAdapter adapter;

    private TextWheelPicker mTextWheelPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.hello_world);

        adapter = new NumericWheelAdapter(this, 1, 15);
        mWheelView = (WheelView) findViewById(R.id.wheel_picker);
        mWheelView.setAdapter(adapter);
        mWheelView.setSelectItem(2);

        mWheelView.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AbsWheelView parentView, int index) {
//                mTextView.setText("" + index);
            }
        });


        ArrayList<String> mList = new ArrayList<String>();
        for (int i = 0; i <= 50; i++){
            mList.add("ok-" + i);
        }
        mTextWheelPicker = (TextWheelPicker) findViewById(R.id.text_wheel_picker);
        mTextWheelPicker.setTextList(mList);
        mTextWheelPicker.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AbsWheelView parentView, int index) {
                mTextView.setText("" + index);
            }
        });
    }
}
