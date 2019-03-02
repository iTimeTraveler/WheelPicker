package com.itimetraveler.widget.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Date;
import java.util.List;

import io.itimetraveler.widget.adapter.NumericWheelAdapter;
import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.IPickerData;
import io.itimetraveler.widget.model.PickerNode;
import io.itimetraveler.widget.model.StringData;
import io.itimetraveler.widget.picker.WheelPicker;
import io.itimetraveler.widget.view.AbsWheelView;
import io.itimetraveler.widget.view.WheelView;


public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    private String s1, s2, s3, s4;

    private WheelView mWheelView;
    private NumericWheelAdapter adapter;

    private WheelPicker mWheelPicker;
    private TextWheelView mTextWheelPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s1 = s2 = s3 = s4 = "";
        mTextView = (TextView) findViewById(R.id.hello_world);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OriginalPickerActivity.class));
            }
        });

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

//        String[] arr = new String[]{"铜仁市",
//                "德江县",
//                "江口县",
//                "思南县",
//                "石阡县",
//                "玉屏侗族自治县",
//                "松桃苗族自治县",
//                "印江土家族苗族自治县",
//                "沿河土家族自治县",
//                "万山特区",
//                "其他"};
//        final List<String> mList = Arrays.asList(arr);
//
//        mTextWheelPicker = (TextWheelView) findViewById(R.id.text_wheel_picker);
//        mTextWheelPicker.setTextList(mList);
//        mTextWheelPicker.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AbsWheelView parentView, int index) {
//                s1 = "" + mList.get(index);
//                updateTextView();
//            }
//        });
//        mTextWheelPicker.setSelectItem(10);
//        mTextWheelPicker.setTheme(TextWheelView.Theme.WHITE);
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
//        getProvincesData();
//        demoPickerAdapter();
//        testDispersed();
        testImageWheel();
    }

    private void testImageWheel() {
        try {
            InputStream is = getAssets().open("flag.json");
            int length = is.available();
            byte[]  buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf8");

            List<PickerNode<IPickerData>> continents = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.optJSONArray("continents");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.optJSONObject(i);
                String pname = o.optString("name");
                PickerNode<IPickerData> pnode = new PickerNode<IPickerData>(new StringData(pname));

                List<PickerNode<IPickerData>> countries = new ArrayList<>();
                JSONArray cityArray = o.optJSONArray("countries");
                for (int j = 0; cityArray != null && j < cityArray.length(); j++) {
                    JSONObject c = cityArray.optJSONObject(j);
                    String cname = c.optString("name");
                    String cflag = c.optString("flag");
                    PickerNode<IPickerData> cnode = new PickerNode<IPickerData>(new FlagData(cname,"flags/" + cflag));
//                    PickerNode<IPickerData> cnode = new PickerNode<IPickerData>(new FlagData("hcg.jpg"));
                    countries.add(cnode);
                }
                pnode.setNextLevel(countries);
                continents.add(pnode);
            }

            mWheelPicker = (WheelPicker) findViewById(R.id.wheel_picker);
            mWheelPicker.setDataSource(continents);
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

    private void testDispersed(){
        mWheelPicker = (WheelPicker) findViewById(R.id.wheel_picker);
        final List<StringData> mHList = new ArrayList<StringData>();
        for (int i = 1; i <= 24; i++){
            mHList.add(new StringData(String.valueOf(i)));
        }
        List<StringData> mMList = new ArrayList<StringData>();
        for (int i = 1; i <= 60; i++){
            mMList.add(new StringData(String.valueOf(i)));
        }
        mWheelPicker.setDataSource(mHList, mMList);
    }

    private void demoPickerAdapter() {
        mWheelPicker = (WheelPicker) findViewById(R.id.wheel_picker);
        PickerAdapter adapter = new PickerAdapter() {
            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return 3;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                switch (component) {
                    case 0:
                        return 10;
                    case 1:
                        return 1;
                    case 2:
                        return 3;
                    default:
                        return 0;
                }
            }

            @Override
            public int widthForComponent(int component) {
                return super.widthForComponent(component);
            }

            @Override
            public int rowHeightForComponent(int component) {
                return super.rowHeightForComponent(component);
            }

            @Override
            public String titleForRow(int row, int component) {
                return super.titleForRow(row, component);
            }

            @Override
            public View viewForRow(int row, int component) {
                return super.viewForRow(row, component);
            }
        };
        mWheelPicker.setAdapter(adapter);
        mWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AbsWheelView parentView, int position) {
            }
        });
    }

    private void getProvincesData() {
        try {
            InputStream is = getAssets().open("provinces.json");
            int length = is.available();
            byte[]  buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf8");

            List<PickerNode<IPickerData>> provinces = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.optJSONObject(i);
                String pname = o.optString("name");
                PickerNode<IPickerData> pnode = new PickerNode<IPickerData>(new StringData(pname));

                List<PickerNode<IPickerData>> city = new ArrayList<>();
                JSONArray cityArray = o.optJSONArray("city");
                for (int j = 0; cityArray != null && j < cityArray.length(); j++) {
                    JSONObject c = cityArray.optJSONObject(j);
                    String cname = c.optString("name");
                    PickerNode<IPickerData> cnode = new PickerNode<IPickerData>(new StringData(cname));

                    List<PickerNode<IPickerData>> area = new ArrayList<>();
                    JSONArray areaArray = c.optJSONArray("area");
                    for (int k = 0; areaArray != null && k < areaArray.length(); k++) {
                        area.add(new PickerNode<IPickerData>(new StringData(areaArray.optString(k))));
                    }
                    cnode.setNextLevel(area);
                    city.add(cnode);
                }
                pnode.setNextLevel(city);
                provinces.add(pnode);
            }

            mWheelPicker = (WheelPicker) findViewById(R.id.wheel_picker);
            mWheelPicker.setDataSource(provinces);
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

}
