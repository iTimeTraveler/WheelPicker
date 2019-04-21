package com.itimetraveler.widget.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import io.itimetraveler.widget.pickerselector.ChineseCityWheelPicker;
import io.itimetraveler.widget.pickerselector.CountryWheelPicker;
import io.itimetraveler.widget.pickerselector.DateWheelPicker;
import io.itimetraveler.widget.pickerselector.TimeWheelPicker;

public class UsageActivity extends AppCompatActivity {

    private TextView countryTv;
    private TextView cityTv;
    private TextView dateTv;
    private TextView timeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);

        // 国家或地区
        countryTv = findViewById(R.id.country);
        countryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountryWheelPicker picker = new CountryWheelPicker(UsageActivity.this);
                picker.setOnCountrySelectListener(new CountryWheelPicker.OnCountrySelectListener() {
                    @Override
                    public void OnCountrySelected(CountryWheelPicker view, String countrySelected) {
                        countryTv.setText(countrySelected);
                    }
                });
                DialogUtil.showDialog(UsageActivity.this, "", picker);
            }
        });

        // 城市
        cityTv = findViewById(R.id.city);
        cityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChineseCityWheelPicker picker = new ChineseCityWheelPicker(UsageActivity.this);
                picker.setOnCitySelectListener(new ChineseCityWheelPicker.OnCitySelectListener() {
                    @Override
                    public void OnCitySelected(ChineseCityWheelPicker view, String province, String city, String area) {
                        cityTv.setText(province + "/" + city + "/" + area);
                    }
                });
                DialogUtil.showDialog(UsageActivity.this, "", picker);
            }
        });

        // 日期
        dateTv = findViewById(R.id.date);
        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateWheelPicker picker = new DateWheelPicker(UsageActivity.this);
                picker.setOnDateChangedListener(new DateWheelPicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DateWheelPicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTv.setText(year + "/" + monthOfYear + "/" + dayOfMonth);
                    }
                });
                DialogUtil.showDialog(UsageActivity.this, "", picker);
            }
        });

        // 日期
        timeTv = findViewById(R.id.time);
        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeWheelPicker picker = new TimeWheelPicker(UsageActivity.this);
                picker.setOnTimeChangedListener(new TimeWheelPicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimeWheelPicker view, int hourOfDay, int minute) {
                        timeTv.setText(hourOfDay + "/" + minute);
                    }
                });
                DialogUtil.showDialog(UsageActivity.this, "", picker);
            }
        });
    }
}
