package io.itimetraveler.widget.picker;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;

import io.itimetraveler.widget.view.AbsWheelView;

/**
 * A delegate for linked wheel picker.
 */
public class LinkedWheelDelegate extends AbstractWheelPickerDelegate{

    public LinkedWheelDelegate(WheelPicker delegator, Context context, PicketOptions options) {
        super(delegator, context, options);
    }

    @Override
    void onPickerItemSelected(AbsWheelView parentView, int row, int componentIdx) {
        // 联动效果
        if (componentIdx < mWheelViews.size() - 1) {
            mWheelViews.get(componentIdx+1).getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onMeasure() {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public Parcelable onSaveInstanceState(Parcelable superState) {
        return null;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
    }
}
