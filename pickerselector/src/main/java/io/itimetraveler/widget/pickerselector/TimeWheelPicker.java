package io.itimetraveler.widget.pickerselector;

import android.content.Context;
import android.util.AttributeSet;

import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2019/3/20.
 */
public class TimeWheelPicker extends WheelPicker {

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
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {
                mOnTimeChangedListener.onTimeChanged(TimeWheelPicker.this, 0, 0);
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
