package io.itimetraveler.widget.pickerselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.StringItemView;
import io.itimetraveler.widget.picker.PicketOptions;
import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2018/7/2.
 */

public class DigitalCipherPicker extends WheelPicker {

    private static final int DEFAULT_COMPONENT_COUNT = 6;
    private static final int DEFAULT_ROW_COUNT = 10;

    private int mCountOfComponents = DEFAULT_COMPONENT_COUNT;
    private int mCountOfRows = DEFAULT_ROW_COUNT;

    public DigitalCipherPicker(Context context) {
        this(context, DEFAULT_COMPONENT_COUNT, DEFAULT_ROW_COUNT);
    }

    public DigitalCipherPicker(Context context, int components, int rows) {
        super(context);
        this.mCountOfComponents = components;
        this.mCountOfRows = rows;
        init();
    }

    public void init() {
        PickerAdapter adapter = new PickerAdapter() {
            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return mCountOfComponents;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                return mCountOfRows;
            }

            @Override
            public View onCreateView(ViewGroup parent, int row, int component) {
                return new StringItemView(String.valueOf(row)).onCreateView(parent);
            }

            @Override
            public void onBindView(ViewGroup parent, View convertView, int row, int component) {
                new StringItemView(String.valueOf(row)).onBindView(parent, convertView, row);
            }
        };

        setOptions(new PicketOptions.Builder()
                .cyclic(true)
                .build());
        setAdapter(adapter);
        setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {
            }
        });
    }
}
