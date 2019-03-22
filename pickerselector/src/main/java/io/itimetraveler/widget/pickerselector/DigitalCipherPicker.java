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

    private int mCountOfComponents = DEFAULT_COMPONENT_COUNT;

    public DigitalCipherPicker(Context context) {
        this(context, null);
    }

    public DigitalCipherPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DigitalCipherPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCipherCount(int count) {
        this.mCountOfComponents = count;
    }

    private void init() {
        PickerAdapter adapter = new PickerAdapter() {
            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return mCountOfComponents;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                return 10;
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
