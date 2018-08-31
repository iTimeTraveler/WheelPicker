package io.itimetraveler.widget.picker;

import android.content.res.Configuration;
import android.os.Parcelable;

import java.util.List;

import io.itimetraveler.widget.adapter.PickerAdapter;

/**
 * A delegate interface that defined the public API of the WheelPicker. Allows different
 * WheelPicker implementations. This would need to be implemented by the WheelPicker delegates
 * for the real behavior.
 */
interface WheelPickerDelegate<T> {

    void setOnDataChangedListener(WheelPicker.OnDataChangedListener onDataChangedListener);

    void setDataSource(List<T>... item1);

    void setAdapter(PickerAdapter adapter);

    void setOnItemSelectedListener(WheelPicker.OnItemSelectedListener listener);

    void onMeasure();
    void onLayout();

    void setEnabled(boolean enabled);
    boolean isEnabled();

    void onConfigurationChanged(Configuration newConfig);

    Parcelable onSaveInstanceState(Parcelable superState);
    void onRestoreInstanceState(Parcelable state);
}
