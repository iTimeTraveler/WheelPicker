package io.itimetraveler.widget.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.itimetraveler.widget.adapter.PickerAdapter;

/**
 * Created by iTimeTraveler on 2018/7/2.
 */
public class WheelPicker extends FrameLayout {

    protected Context mContext;
    private IWheelPickerDelegate mDelegate;
    protected PicketOptions mOptions = new PicketOptions.Builder().build();

    public WheelPicker(@NonNull Context context) {
        this(context, null);
    }

    public WheelPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        if (mDelegate == null) {
            mDelegate = mOptions != null && mOptions.isLinkage() ?
                    new LinkedWheelDelegate(this, mContext, mOptions) :
                    new DispersedWheelDelegate(this, mContext, mOptions);
        }
    }

    public void setAdapter(PickerAdapter pickerAdapter) {
        mDelegate = mOptions != null && mOptions.isLinkage() ?
                new LinkedWheelDelegate(this, mContext, mOptions) :
                new DispersedWheelDelegate(this, mContext, mOptions);
        mDelegate.setAdapter(pickerAdapter);
    }

    public void setOptions(PicketOptions options) {
        this.mOptions = options;
    }

    /**
     * Set the listener that will receive notifications when the wheel view
     * finishes scrolling and select an option automatically.
     * @param l the item selected listener
     */
    public void setOnItemSelectedListener(OnItemSelectedListener l) {
        mDelegate.setOnItemSelectedListener(l);
    }

    public void setSelection(int component, int row) {
        mDelegate.setSelection(component, row);
    }

    public int[] getSelectedPositions() {
        return mDelegate.getSelectedPositions();
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
        void onDataChanged(WheelPicker picker, int[] indexArray);
    }

    public interface OnItemSelectedListener {

        /**
         * Callback method to be invoked while the wheel view's item is being selected.
         * @param parentView
         * @param position
         */
        public void onItemSelected(WheelPicker parentView, int[] position);
    }
}
