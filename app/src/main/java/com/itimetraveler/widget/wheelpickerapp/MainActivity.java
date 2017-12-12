package com.itimetraveler.widget.wheelpickerapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import adapter.NumericWheelAdapter;
import view.WheelView;


public class MainActivity extends AppCompatActivity {
    WheelView mWheelView;
    NumericWheelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new NumericWheelAdapter(this, 1, 4);
        mWheelView = (WheelView) findViewById(R.id.wheel_picker);
        mWheelView.setAdapter(adapter);
        mWheelView.setSelectItem(2);
    }
}
