package com.itimetraveler.widget.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.itimetraveler.widget.adapter.NumericWheelAdapter;
import io.itimetraveler.widget.picker.TextWheelPicker;
import io.itimetraveler.widget.picker.WheelPicker;
import io.itimetraveler.widget.view.AbsWheelView;
import io.itimetraveler.widget.view.WheelView;


public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    private String s1, s2, s3, s4;

    private WheelView mWheelView;
    private NumericWheelAdapter adapter;

    private WheelPicker mWheelPicker;
    private TextWheelPicker mTextWheelPicker;
    private TextWheelPicker mDateTextWheelPicker;
    private TextWheelPicker mHourTextWheelPicker;
    private TextWheelPicker mMinuteTextWheelPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s1 = s2 = s3 = s4 = "";
        mTextView = (TextView) findViewById(R.id.hello_world);

//        io.itimetraveler.widget.adapter = new NumericWheelAdapter(this, 1, 15);
//        mWheelView = (WheelView) findViewById(R.id.wheel_view);
//        mWheelView.setAdapter(io.itimetraveler.widget.adapter);
//        mWheelView.setSelectItem(2);
//
//        mWheelView.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AbsWheelView parentView, int index) {
//                mTextView.setText("" + index);
//            }
//        });


//		final List<String> mList = new ArrayList<String>();
//        for (int i = 0; i <= 5000; i++){
//            mList.add("text " + i + "");
//        }
//
//        mTextWheelPicker = (TextWheelPicker) findViewById(R.id.text_wheel_picker);
//        mTextWheelPicker.setTextList(mList);
//        mTextWheelPicker.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AbsWheelView parentView, int index) {
//                s1 = "" + mList.get(index);
//                updateTextView();
//            }
//        });
//        mTextWheelPicker.setSelectItem(10);
//        mTextWheelPicker.setTheme(TextWheelPicker.Theme.white);
//
//
//        //日期
//        int daysCount = 500;
//        final List<String> mDateList = generateDateList(daysCount);
//        //小时
//        final List<String> mHList = new ArrayList<String>();
//        for (int i = 1; i <= 24; i++){
//            mHList.add("" + String.format("%02d", i) + "");
//        }
//        //分钟
//        final List<String> mMList = new ArrayList<String>();
//        for (int i = 1; i <= 60; i++){
//            mMList.add("" + String.format("%02d", i)  + "");
//        }
//
//        mDateTextWheelPicker = (TextWheelPicker) findViewById(R.id.date_text_wheel_picker);
//        mDateTextWheelPicker.setTextList(mDateList);
//        mDateTextWheelPicker.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AbsWheelView parentView, int index) {
//                s2 = "" + mDateList.get(index);
//                updateTextView();
//            }
//        });
//        mDateTextWheelPicker.setCameraOffsetX(-70);
//        mDateTextWheelPicker.setSelectItem(daysCount);
//        mDateTextWheelPicker.setTheme(TextWheelPicker.Theme.black);
//
//
//        //小时
//        mHourTextWheelPicker = (TextWheelPicker) findViewById(R.id.hour_text_wheel_picker);
//        mHourTextWheelPicker.setTextList(mHList);
//        mHourTextWheelPicker.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AbsWheelView parentView, int index) {
//                s3 = "" + mHList.get(index);
//                updateTextView();
//            }
//        });
//        mHourTextWheelPicker.setCameraOffsetX(70);
//        mHourTextWheelPicker.setSelectItem(0);
//        mHourTextWheelPicker.setTheme(TextWheelPicker.Theme.black);
//
//        //分钟
//        mMinuteTextWheelPicker = (TextWheelPicker) findViewById(R.id.minute_text_wheel_picker);
//        mMinuteTextWheelPicker.setTextList(mMList);
//        mMinuteTextWheelPicker.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AbsWheelView parentView, int index) {
//                s4 = "" + mMList.get(index);
//                updateTextView();
//            }
//        });
//        mMinuteTextWheelPicker.setCameraOffsetX(100);
//        mMinuteTextWheelPicker.setSelectItem(1);
//        mMinuteTextWheelPicker.setTheme(TextWheelPicker.Theme.black);
//
//        mTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, OriginalPickerActivity.class));
//            }
//        });


//        Collection[] data = new Collection[]{mDateList, mHList, mMList};
//        mWheelPicker = (WheelPicker) findViewById(R.id.wheel_picker);
//        mWheelPicker.setDataSource(getProvincesData());
//        mWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AbsWheelView parentView, int position) {
//                s3 = "" + position;
//                updateTextView();
//            }
//        });
        getProvincesData();
    }

    private void getProvincesData() {
        try {
            InputStream is = getAssets().open("provinces.json");
            int length = is.available();
            byte[]  buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf8");

            List<String> provinces = new ArrayList<>();
            List<List<String>> cities = new ArrayList<>();
            List<List<List<String>>> areas = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.optJSONObject(i);
                String pname = o.optString("name");

                List<String> city = new ArrayList<>();
                List<List<String>> city_areas = new ArrayList<>();
                JSONArray cityArray = o.optJSONArray("city");
                for (int j = 0; cityArray != null && j < cityArray.length(); j++) {
                    JSONObject c = cityArray.optJSONObject(j);
                    city.add(c.optString("name"));

                    List<String> area = new ArrayList<>();
                    JSONArray areaArray = c.optJSONArray("area");
                    for (int k = 0; areaArray != null && k < areaArray.length(); k++) {
                        area.add(areaArray.optString(k));
                    }
                    city_areas.add(area);
                }
                areas.add(city_areas);
                cities.add(city);
                provinces.add(pname);
            }

            mWheelPicker = (WheelPicker) findViewById(R.id.wheel_picker);
            mWheelPicker.setDataSource(provinces, cities, areas);
            mWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AbsWheelView parentView, int position) {
                    s3 = "" + position;
                    updateTextView();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private List<String> generateDateList(int daysCount){
        Format dateFormat = new SimpleDateFormat("MM月dd日");
        Format weekFormat = new SimpleDateFormat("E");
        Date today = new Date();

        final int todayIdx = daysCount;
        String[] arr = new String[daysCount * 2 + 1];
        for(int i = daysCount; i > 0; i--){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, -i);   // 今天-1天
            arr[todayIdx - i] = dateFormat.format(c.getTime()) + " " + weekFormat.format(c.getTime());

            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            c1.add(Calendar.DAY_OF_MONTH, i);   // 今天+1天
            arr[todayIdx + i] = dateFormat.format(c1.getTime()) + " " + weekFormat.format(c1.getTime());
        }
        arr[todayIdx] = dateFormat.format(today) + " 今天";

        return Arrays.asList(arr);
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


//    class City {
//        String name;
//        List<String> countries;
//
//        List<String> getCitySubNames() {
//            return countries;
//        }
//    }
//
//    class Province {
//        String name;
//        List<City> cities;
//
//        List<String> getSubNames() {
//            List<String> r = new ArrayList<>();
//            for (int i = 0; i < cities.size(); i++) {
//                r.add(cities.get(i).name);
//            }
//            return r;
//        }
//    }
}
