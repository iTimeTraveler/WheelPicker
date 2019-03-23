package io.itimetraveler.widget.pickerselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.StringItemView;
import io.itimetraveler.widget.picker.PicketOptions;
import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2019/3/20.
 */
public class DateWheelPicker extends WheelPicker {

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;

    private final DateFormat mDateFormat = new SimpleDateFormat(DATE_FORMAT);

    private Calendar mCurrentDate;
    private Calendar mMinDate;
    private Calendar mMaxDate;
    private Calendar mTempDate;

    private OnDateChangedListener mOnDateChangedListener;

    public DateWheelPicker(Context context) {
        this(context, null);
    }

    public DateWheelPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateWheelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // initialization based on locale
        setCurrentLocale(Locale.getDefault());

        // set the min date giving priority of the minDate over startYear
        mTempDate.clear();
        mTempDate.set(DEFAULT_START_YEAR, 0, 1);
        setMinDate(mTempDate.getTimeInMillis());

        // set the max date giving priority of the maxDate over endYear
        mTempDate.clear();
        mTempDate.set(DEFAULT_END_YEAR, 11, 31);
        setMaxDate(mTempDate.getTimeInMillis());

        init(mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
                mCurrentDate.get(Calendar.DAY_OF_MONTH), null);
    }

    /**
     * Initialize the state. If the provided values designate an inconsistent
     * date the values are normalized before updating the spinners.
     *
     * @param year The initial year.
     * @param monthOfYear The initial month <strong>starting from zero</strong>.
     * @param dayOfMonth The initial day of the month.
     * @param onDateChangedListener How user is notified date is changed by
     *            user, can be null.
     */
    public void init(int year, int monthOfYear, int dayOfMonth, OnDateChangedListener onDateChangedListener) {
        setDate(year, monthOfYear, dayOfMonth);
        mOnDateChangedListener = onDateChangedListener;

        init0();
    }

    public void setMinDate(long minDate) {
        mTempDate.setTimeInMillis(minDate);
        if (mTempDate.get(Calendar.YEAR) == mMinDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) == mMinDate.get(Calendar.DAY_OF_YEAR)) {
            // Same day, no-op.
            return;
        }
        mMinDate.setTimeInMillis(minDate);
    }

    public void setMaxDate(long maxDate) {
        mTempDate.setTimeInMillis(maxDate);
        if (mTempDate.get(Calendar.YEAR) == mMaxDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) == mMaxDate.get(Calendar.DAY_OF_YEAR)) {
            // Same day, no-op.
            return;
        }
        mMaxDate.setTimeInMillis(maxDate);
    }

    public int getYear() {
        return mCurrentDate.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCurrentDate.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        return mCurrentDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Sets the current locale.
     * @param locale The current locale.
     */
    protected void setCurrentLocale(Locale locale) {
        mTempDate = getCalendarForLocale(mTempDate, locale);
        mMinDate = getCalendarForLocale(mMinDate, locale);
        mMaxDate = getCalendarForLocale(mMaxDate, locale);
        mCurrentDate = getCalendarForLocale(mCurrentDate, locale);
    }

    /**
     * Gets a calendar for locale bootstrapped with the value of a given calendar.
     *
     * @param oldCalendar The old calendar.
     * @param locale The locale.
     */
    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        } else {
            final long currentTimeMillis = oldCalendar.getTimeInMillis();
            Calendar newCalendar = Calendar.getInstance(locale);
            newCalendar.setTimeInMillis(currentTimeMillis);
            return newCalendar;
        }
    }
    
    private void init0() {
        PickerAdapter adapter = new PickerAdapter() {

            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return 3;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                switch (component) {
                    case 0:
                        return mMaxDate.get(Calendar.YEAR) - mMinDate.get(Calendar.YEAR);
                    case 1:
                        return mCurrentDate.getActualMaximum(Calendar.MONTH) + 1;
                    case 2:
                        return mCurrentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                }
                return 0;
            }

            @Override
            public View onCreateView(ViewGroup parent, int row, int component) {
                switch (component) {
                    case 0:
                        return new StringItemView(String.valueOf(mMinDate.get(Calendar.YEAR) + row)).onCreateView(parent);
                    case 1:
                        return new StringItemView(String.valueOf(row + 1)).onCreateView(parent);
                    case 2:
                        return new StringItemView(String.valueOf(row + 1)).onCreateView(parent);
                }
                return null;
            }

            @Override
            public void onBindView(ViewGroup parent, View convertView, int row, int component) {
                switch (component) {
                    case 0:
                        new StringItemView(String.valueOf(mMinDate.get(Calendar.YEAR) + row)).onBindView(parent, convertView, row);
                        break;
                    case 1:
                        new StringItemView(String.valueOf(row + 1)).onBindView(parent, convertView, row);
                        break;
                    case 2:
                        new StringItemView(String.valueOf(row + 1)).onBindView(parent, convertView, row);
                        break;
                }
            }

            @Override
            public String labelOfComponent(int component) {
                switch (component) {
                    case 0:
                        return "年";
                    case 1:
                        return "月";
                    case 2:
                        return "日";
                }
                return "";
            }
        };

        setOptions(new PicketOptions.Builder()
                .linkage(true)
                .build());
        setAdapter(adapter);

        int year = mCurrentDate.get(Calendar.YEAR) - mMinDate.get(Calendar.YEAR);
        setSelection(0, year);
        int month = mCurrentDate.get(Calendar.MONTH);
        setSelection(1, month);
        int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        setSelection(2, day - 1);

        setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {
                if (position == null || position.length <= 0) return;

                // year
                int newYear = position[0];
                mTempDate.set(Calendar.YEAR, mMinDate.get(Calendar.YEAR) + newYear);

                // month
                int newMonth = position[1];
                mTempDate.set(Calendar.MONTH, newMonth);

                // take care of wrapping of days and months to update greater fields
                int newDay = position[2];
                mTempDate.set(Calendar.DAY_OF_MONTH, newDay + 1);

                // now set the date to the adjusted one
                setDate(mTempDate.get(Calendar.YEAR), mTempDate.get(Calendar.MONTH),
                        mTempDate.get(Calendar.DAY_OF_MONTH));

                if (mOnDateChangedListener != null) {
                    mOnDateChangedListener.onDateChanged(DateWheelPicker.this,
                            getYear(), getMonth(), getDayOfMonth());
                }
            }
        });
    }

    private void setDate(int year, int month, int dayOfMonth) {
        mCurrentDate.set(year, month, dayOfMonth);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        } else if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
    }

    /**
     * Set the callback that indicates the date has been adjusted by the user.
     *
     * @param onDateChangedListener How user is notified date is changed by
     *            user, can be null.
     */
    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        mOnDateChangedListener = onDateChangedListener;
    }

    /**
     * The callback used to indicate the user changed the date.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         *
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *            with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateChanged(DateWheelPicker view, int year, int monthOfYear, int dayOfMonth);
    }
}
