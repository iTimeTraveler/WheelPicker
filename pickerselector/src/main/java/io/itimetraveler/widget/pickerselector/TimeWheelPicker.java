package io.itimetraveler.widget.pickerselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.StringItemView;
import io.itimetraveler.widget.picker.PicketOptions;
import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2019/3/20.
 */
public class TimeWheelPicker extends WheelPicker {

    public static final int TYPE_YEAR = 1 << 0;
    public static final int TYPE_MONTH = 1 << 1;
    public static final int TYPE_DAY = 1 << 2;
    /** 日期聚合 */
    public static final int TYPE_MIXED_DATE = 1 << 3;
    /** 时间聚合 */
    public static final int TYPE_MIXED_TIME = 1 << 4;

    public static final int TYPE_HOUR = 1 << 5;
    public static final int TYPE_MINUTE = 1 << 6;
    public static final int TYPE_AM_PM = 1 << 7;

    // 日期：年月日
    public static final int TYPE_DATE = TYPE_YEAR | TYPE_MONTH | TYPE_DAY;
    // 时间：小时、分钟、AM/PM
    public static final int TYPE_TIME = TYPE_HOUR | TYPE_MINUTE | TYPE_AM_PM;
    // 全部
    public static final int TYPE_ALL = TYPE_DATE | TYPE_TIME;

    public static final String[] AM_PM_DESC = {"AM", "PM"};
    public static final NumberFormat DEFAULT_MINUTE_FORMAT = new DecimalFormat("###00");
    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日");
    public static final DateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("HH:mm");
    public static final Format dateFormat = new SimpleDateFormat("MM月dd日");
    public static final Format weekFormat = new SimpleDateFormat("E");

    private Calendar mCurrentDate;

    private int mType = TYPE_MIXED_DATE | TYPE_TIME;
    private Context mContext;
    private int mCount;
    private List<Integer> mTypeMap = new ArrayList<Integer>();
    private List<Calendar> mMixedDateList = new ArrayList<Calendar>();
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

    public void setDisplayType(int type) {
        mType = type;
        init();
    }

    private void init() {
        parseType();
        PickerAdapter adapter = new PickerAdapter() {

            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return mCount;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                int type = mTypeMap.get(component);
                switch (type) {
                    case TYPE_YEAR:
                        return 100;
                    case TYPE_MONTH:
                        return 12;
                    case TYPE_DAY:
                        return 30;
                    case TYPE_HOUR:
                        return 12;
                    case TYPE_MINUTE:
                        return 60;
                    case TYPE_AM_PM:
                        return 2;
                    case TYPE_MIXED_DATE:
                        return 365 * 2;
                    case TYPE_MIXED_TIME:
                        break;
                }
                return 0;
            }

            @Override
            public View onCreateView(ViewGroup parent, int row, int component) {
                int type = mTypeMap.get(component);
                return new StringItemView(getDesc(type, row)).onCreateView(parent);
            }

            @Override
            public void onBindView(ViewGroup parent, View convertView, int row, int component) {
                int type = mTypeMap.get(component);
                new StringItemView(getDesc(type, row)).onBindView(parent, convertView, row);
            }

            @Override
            public String labelOfComponent(int component) {
//                int type = mTypeMap.get(component);
//                switch (type) {
//                    case TYPE_YEAR:
//                        return "年";
//                    case TYPE_MONTH:
//                        return "月";
//                    case TYPE_DAY:
//                        return "日";
//                    case TYPE_HOUR:
//                        return "时";
//                    case TYPE_MINUTE:
//                        return "分";
//                    case TYPE_MIXED_DATE:
//                        break;
//                    case TYPE_MIXED_TIME:
//                        break;
//                }
                return "";
            }
        };

        setOptions(new PicketOptions.Builder()
                .dividedEqually(false)
                .build());
        setAdapter(adapter);

        for (int i = 0; i < mCount; i++) {
            int type = mTypeMap.get(i);
            switch (type) {
                case TYPE_YEAR:
                    setSelection(i, 365);
                    break;
                case TYPE_MONTH:
                    break;
                case TYPE_DAY:
                    break;
                case TYPE_HOUR:
                    break;
                case TYPE_MINUTE:
                    break;
                case TYPE_AM_PM:
                    break;
                case TYPE_MIXED_DATE:
                    break;
                case TYPE_MIXED_TIME:
                    break;
            }
        }

        setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {
//                int count = position.length;
//                for (int i = 0; i < count; i++) {
//                    int type = mTypeMap.get(component);
//                    switch (type) {
//                        case TYPE_YEAR:
//                            return 100;
//                        case TYPE_MONTH:
//                            return 12;
//                        case TYPE_DAY:
//                            return 30;
//                        case TYPE_HOUR:
//                            return 12;
//                        case TYPE_MINUTE:
//                            return 60;
//                        case TYPE_AM_PM:
//                            return 2;
//                        case TYPE_MIXED_DATE:
//                            return 365 * 2;
//                        case TYPE_MIXED_TIME:
//                            break;
//                    }
//                }

                if (mOnTimeChangedListener != null) {
                    mOnTimeChangedListener.onTimeChanged(TimeWheelPicker.this, position[0], position[1]);
                }
            }
        });
    }

    private void parseType() {
        mCount = Integer.bitCount(mType);

        for (int i = 0; i < 32; i++) {
            int t = ((mType << (31 - i)) >>> 31) << i;
            if ((t & (1 << i)) > 0) {
                mTypeMap.add(t);
            }
        }

        if ((mType & TYPE_MIXED_DATE) > 0) {
            mMixedDateList = generateDateList(365);
        }
    }

    private List<Calendar> generateDateList(int daysCount){
        final int todayIdx = daysCount;
        Calendar[] arr = new Calendar[daysCount * 2 + 1];
        for(int i = daysCount; i > 0; i--){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, -i);   // 今天-1天
            arr[todayIdx - i] = c;

            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            c1.add(Calendar.DAY_OF_MONTH, i);   // 今天+1天
            arr[todayIdx + i] = c;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        arr[todayIdx] = c;

        return Arrays.asList(arr);
    }

    private String getDesc(int type, int row) {
        switch (type) {
            case TYPE_YEAR:
                return String.valueOf(row);
            case TYPE_MONTH:
            case TYPE_DAY:
            case TYPE_HOUR:
                return String.valueOf(row);
            case TYPE_MINUTE:
                return DEFAULT_MINUTE_FORMAT.format(row);
            case TYPE_AM_PM:
                return AM_PM_DESC[row];
            case TYPE_MIXED_DATE:
                Calendar c = mMixedDateList.get(row);
                return dateFormat.format(c.getTime()) + " " + weekFormat.format(c.getTime());
            case TYPE_MIXED_TIME:
                break;
        }
        return null;
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

//        void onTimeChanged(TimeWheelPicker view, Date date);
    }
}
