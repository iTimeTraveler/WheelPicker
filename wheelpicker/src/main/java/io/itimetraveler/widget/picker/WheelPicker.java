package io.itimetraveler.widget.picker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.List;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.IPickerData;
import io.itimetraveler.widget.model.PickerNode;
import io.itimetraveler.widget.view.AbsWheelView;

/**
 * Created by iTimeTraveler on 2018/7/2.
 */
public class WheelPicker extends FrameLayout {

    private Context mContext;
    private IWheelPickerDelegate mDelegate;

    public WheelPicker(@NonNull Context context) {
        this(context, null);
    }

    public WheelPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public <N extends PickerNode> void setDataSource(List<N> nodeList) {
        mDelegate = new LinkedWheelDelegate(this, mContext);
        mDelegate.setDataSource(nodeList);
    }

    public <D extends IPickerData> void setDataSource(List<D>... dataArray) {
        mDelegate = new DispersedWheelDelegate(this, mContext);
        mDelegate.setDataSource(dataArray);
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
     * 是否开启循环
     * @param cycle
     */
    public void enableCyclic(boolean cycle) {
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


    public Builder newBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        boolean enableLinkage;
        boolean enableCyclic;

        public Builder() {
            enableLinkage = false;
            enableCyclic = false;
        }

        Builder(WheelPicker wheelPicker) {
        }
    }
}
