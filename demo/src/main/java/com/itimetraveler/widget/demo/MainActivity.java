package com.itimetraveler.widget.demo;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import io.itimetraveler.widget.adapter.NumericWheelAdapter;
import io.itimetraveler.widget.model.IPickerItemView;
import io.itimetraveler.widget.model.PickerNode;
import io.itimetraveler.widget.model.StringItemView;
import io.itimetraveler.widget.picker.TextSingleWheelPicker;
import io.itimetraveler.widget.picker.WheelPicker;
import io.itimetraveler.widget.pickerselector.ChineseCityWheelPicker;
import io.itimetraveler.widget.pickerselector.DigitalCipherPicker;
import io.itimetraveler.widget.view.WheelView;


public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    private String s1, s2, s3, s4;

    private WheelView mWheelView;
    private NumericWheelAdapter adapter;

    private WheelPicker mWheelPicker;
    private ChineseCityWheelPicker mChineseCityWheelPicker;
    private DigitalCipherPicker mDigitalCipherPicker;
    private TextSingleWheelPicker mTextSingleWheelPicker;

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
//
        getProvincesData();
//        demoPickerAdapter();
//        testDispersed();
//        testImageWheel();
    }

    private void testImageWheel() {
        try {
            InputStream is = getAssets().open("flag.json");
            int length = is.available();
            byte[]  buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf8");

            List<PickerNode<IPickerItemView>> continents = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.optJSONArray("continents");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.optJSONObject(i);
                String pname = o.optString("name");
                PickerNode<IPickerItemView> pnode = new PickerNode<IPickerItemView>(new StringItemView(pname));

                List<PickerNode<IPickerItemView>> countries = new ArrayList<>();
                JSONArray cityArray = o.optJSONArray("countries");
                for (int j = 0; cityArray != null && j < cityArray.length(); j++) {
                    JSONObject c = cityArray.optJSONObject(j);
                    String cname = c.optString("name");
                    String cflag = c.optString("flag");
                    PickerNode<IPickerItemView> cnode = new PickerNode<IPickerItemView>(new FlagItemView(cname,"flags/" + cflag));
//                    PickerNode<IPickerItemView> cnode = new PickerNode<IPickerItemView>(new FlagItemView("hcg.jpg"));
                    countries.add(cnode);
                }
                pnode.setNextLevel(countries);
                continents.add(pnode);
            }

            mWheelPicker = (WheelPicker) findViewById(R.id.wheel_picker);
            mWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker parentView, int[] position) {
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

    private void getProvincesData() {
        mChineseCityWheelPicker = (ChineseCityWheelPicker) findViewById(R.id.wheel_picker);
        mDigitalCipherPicker = (DigitalCipherPicker) findViewById(R.id.digital_cipher_picker);
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
