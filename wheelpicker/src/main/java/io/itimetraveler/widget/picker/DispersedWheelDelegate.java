package io.itimetraveler.widget.picker;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;

import io.itimetraveler.widget.view.AbsWheelView;


/**
 * A delegate for Dispersed wheel picker.
 */
class DispersedWheelDelegate extends AbstractWheelPickerDelegate {

    public DispersedWheelDelegate(WheelPicker delegator, Context context, PicketOptions options) {
        super(delegator, context, options);
    }

    @Override
    void onPickerItemSelected(AbsWheelView parentView, int row, int componentIdx) {
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
