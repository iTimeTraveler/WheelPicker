package io.itimetraveler.widget.pickerselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.StringItemView;
import io.itimetraveler.widget.picker.PicketOptions;
import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2019/3/20.
 */
public class TimeWheelPicker extends WheelPicker {

    public static final int TYPE_YEAR = 0x01;
    public static final int TYPE_MONTH = 0x02;
    public static final int TYPE_DAY = 0x04;
    public static final int TYPE_HOUR = 0x08;
    public static final int TYPE_MINUTE = 0x10;
    /** 日期聚合 */
    public static final int TYPE_MIXED_DATE = 0x20;
    /** 时间聚合 */
    public static final int TYPE_MIXED_TIME = 0x40;

    // 日期：年月日
    public static final int TYPE_DATE = TYPE_YEAR | TYPE_MONTH | TYPE_DAY;
    // 时间：小时、分钟
    public static final int TYPE_TIME = TYPE_HOUR | TYPE_MINUTE;
    // 全部
    public static final int TYPE_ALL = TYPE_DATE | TYPE_TIME;

    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日");
    public static final DateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("HH:mm");


    private Context mContext;
    private OnTimeChangedListener mOnTimeChangedListener;

    public TimeWheelPicker(Context context) {
        this(context, null);
    }

    public TimeWheelPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeWheelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        PickerAdapter adapter = new PickerAdapter() {

            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return 2;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                switch (component) {
                    case 0:
                        return 24;
                    case 1:
                        return 60;
                }
                return 0;
            }

            @Override
            public View onCreateView(ViewGroup parent, int row, int component) {
                switch (component) {
                    case 0:
                        return new StringItemView(String.valueOf(row)).onCreateView(parent);
                    case 1:
                        return new StringItemView(String.valueOf(row)).onCreateView(parent);
                }
                return null;
            }

            @Override
            public void onBindView(ViewGroup parent, View convertView, int row, int component) {
                switch (component) {
                    case 0:
                        new StringItemView(String.valueOf(row)).onBindView(parent, convertView, row);
                        break;
                    case 1:
                        new StringItemView(String.valueOf(row)).onBindView(parent, convertView, row);
                        break;
                }
            }

            @Override
            public String labelOfComponent(int component) {
                switch (component) {
                    case 0:
                        return "时";
                    case 1:
                        return "分";
                }
                return "";
            }
        };

        setOptions(new PicketOptions.Builder()
                .dividedEqually(false)
                .build());
        setAdapter(adapter);

        setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {
                if (mOnTimeChangedListener != null) {
                    mOnTimeChangedListener.onTimeChanged(TimeWheelPicker.this, position[0], position[1]);
                }
            }
        });
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }

    /**
     * The callback interface used to indicate the time has been adjusted.
     */
    public interface OnTimeChangedListener {

        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The current hour.
         * @param minute The current minute.
         */
        void onTimeChanged(TimeWheelPicker view, int hourOfDay, int minute);
    }
}
