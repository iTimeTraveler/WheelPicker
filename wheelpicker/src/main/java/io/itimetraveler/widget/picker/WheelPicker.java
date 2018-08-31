package io.itimetraveler.widget.picker;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Collection;
import java.util.List;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.view.AbsWheelView;
import io.itimetraveler.widget.view.WheelView;

/**
 * Created by iTimeTraveler on 2018/7/2.
 */
public class WheelPicker extends FrameLayout {

    private WheelPickerDelegate mDelegate;

    public WheelPicker(@NonNull Context context) {
        this(context, null);
    }

    public WheelPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDelegate = new DisperseWheelDelegate(this, context);
    }

    public <T> void setDataSource(List<T> item1,
                                  List<List<T>> item2,
                                  List<List<List<T>>> item3) {
        mDelegate.setDataSource(item1, item2, item3);
    }


    public void setAdapter(PickerAdapter adapter) {
        mDelegate.setAdapter(adapter);
    }

    /**
     * 是否开启联动效果
     */
    public void enableLinkage(boolean linkage) {
    }

    /**
     * Set the listener that will receive notifications when the wheel view
     * finishes scrolling and select an option automatically.
     * @param l the item selected listener
     */
    public void setOnItemSelectedListener(OnItemSelectedListener l) {
        mDelegate.setOnItemSelectedListener(l);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDelegate.onMeasure();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDelegate.onLayout();
    }

    /**
     * The callback used to indicate the user changed the data.
     */
    public interface OnDataChangedListener {
        void onDataChanged(WheelPicker view, String data);
    }

    public interface OnItemSelectedListener {

        /**
         * Callback method to be invoked while the wheel view's item is being selected.
         * @param parentView
         * @param position
         */
        public void onItemSelected(AbsWheelView parentView, int position);
    }
}
