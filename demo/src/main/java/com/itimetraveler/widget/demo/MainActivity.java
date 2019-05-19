package com.itimetraveler.widget.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itimetraveler.widget.demo.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.StringItemView;
import io.itimetraveler.widget.picker.PicketOptions;
import io.itimetraveler.widget.picker.WheelPicker;
import io.itimetraveler.widget.pickerselector.ChineseCityWheelPicker;
import io.itimetraveler.widget.pickerselector.CountDownWheelPicker;
import io.itimetraveler.widget.pickerselector.CountryWheelPicker;
import io.itimetraveler.widget.pickerselector.DateWheelPicker;
import io.itimetraveler.widget.pickerselector.TextSingleWheelPicker;
import io.itimetraveler.widget.pickerselector.TimeWheelPicker;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    private String s1, s2, s3, s4;

    private TextSingleWheelPicker mTextSingleWheelPicker;
    private WheelPicker mWheelPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s1 = s2 = s3 = s4 = "";
        mTextView = (TextView) findViewById(R.id.hello_world);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UsageActivity.class));
            }
        });

        // 城市选择器
        findViewById(R.id.chinese_city_wheelpicker_button)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChineseCityWheelPicker chineseCityWheelPicker = new ChineseCityWheelPicker(MainActivity.this);
                showDialog("ChineseCityWheelPicker\n(城市选择器)", chineseCityWheelPicker);
            }
        });

        // 国家选择器
        findViewById(R.id.country_wheelpicker_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog("CountryWheelPicker\n(国家选择器)", new CountryWheelPicker(MainActivity.this));
                    }
                });

        // 时间选择器
        findViewById(R.id.time_wheelpicker_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimeWheelPicker picker = new TimeWheelPicker(MainActivity.this);
                        picker.setOnTimeChangedListener(new TimeWheelPicker.OnTimeChangedListener() {
                            @Override
                            public void onTimeChanged(TimeWheelPicker view, Calendar date) {
                                Toast.makeText(MainActivity.this, TimeWheelPicker.DEFAULT_TIME_FORMAT.format(date.getTime()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        showDialog("TimeWheelPicker\n(时间选择器)", picker);
                    }
                });

        // 日期选择器
        findViewById(R.id.date_wheelpicker_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateWheelPicker picker = new DateWheelPicker(MainActivity.this);
                        picker.setOnDateChangedListener(new DateWheelPicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DateWheelPicker view, int year, int monthOfYear, int dayOfMonth) {
                                Toast.makeText(MainActivity.this,
                                        year + "/" + monthOfYear + "/" + dayOfMonth,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        showDialog("DateWheelPicker\n(日期选择器)", picker);
                    }
                });

        findViewById(R.id.digital_wheelpicker_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CountDownWheelPicker countDownWheelPicker = new CountDownWheelPicker(MainActivity.this, 11);
                        showDialog("CountDownWheelPicker\n(倒计时器)", countDownWheelPicker);
                        countDownWheelPicker.startCountDown();
                    }
                });


        final List<String> mList = Arrays.asList("铜仁市",
                "德江县",
                "江口县",
                "思南县",
                "石阡县",
                "玉屏侗族自治县",
                "松桃苗族自治县",
                "印江土家族苗族自治县",
                "沿河土家族自治县",
                "万山特区",
                "其他");

        mTextSingleWheelPicker = (TextSingleWheelPicker) findViewById(R.id.text_wheel_picker);
        mTextSingleWheelPicker.setTextList(mList);
        mTextSingleWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] index) {
                s1 = "" + mList.get(index[0]);
                updateTextView();
            }
        });
//        mTextSingleWheelPicker.setSelection(10);
        mTextSingleWheelPicker.setTheme(TextSingleWheelPicker.Theme.white);


        mWheelPicker = (WheelPicker) findViewById(R.id.wheel_picker);
        mWheelPicker.setOptions(new PicketOptions.Builder()
                .linkage(false)                                             // 是否联动
                .dividedEqually(false)                                      // 每列宽度是否均等分
                .backgroundColor(Color.parseColor("#000000"))     // 背景颜色
                .dividerColor(Color.parseColor("#999999"))        // 选中项分割线颜色
                .build());

        PickerAdapter adapter = new PickerAdapter() {
            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                // 需要多少列
                return 2;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                // 某一列有多少行数据
                switch (component) {
                    case 0:
                        return 10;
                    case 1:
                        return mList.size();
                }
                return 0;
            }

            @Override
            public View onCreateView(ViewGroup parent, int row, int component) {
                // 某行某列显示的View，String 数据可使用默认 StringItemView 类
                String str = "";
                switch (component) {
                    case 0:
                        str = "" + row;
                        break;
                    case 1:
                        str = mList.get(row);
                        break;
                }
                return new StringItemView(String.valueOf(str)).onCreateView(parent);
            }

            @Override
            public void onBindView(ViewGroup parent, View convertView, int row, int component) {
                // 回收的View，仅需根据某行某列刷新数据。String 数据可使用默认 StringItemView 类
                String str = "";
                switch (component) {
                    case 0:
                        str = "" + row;
                        break;
                    case 1:
                        str = mList.get(row);
                        break;
                }
                new StringItemView(String.valueOf(str)).onBindView(parent, convertView, row);
            }
        };
        mWheelPicker.setAdapter(adapter);
        mWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {
                // 选中后的回调
                s1 = "" + position[0];
                s2 = mList.get(position[1]);
                updateTextView();
            }
        });
    }


    private void showDialog(String title, View v) {
        DialogUtil.showDialog(MainActivity.this, title, v);
    }

    private void updateTextView(){
        mTextView.setText(s1 + "  |  " + s2 + "  |  " + s3 + "  |  " + s4);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimeUtils.getInstance().setEndTime("onResume");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        TimeUtils.getInstance().setEndTime("onWindowFocusChanged > hasFocus:" + hasFocus);
    }

}
