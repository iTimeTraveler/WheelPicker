package io.itimetraveler.widget.picker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.List;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.view.WheelView;

/**
 * An abstract class which can be used as a start for WheelPicker implementations
 */
abstract class AbstractWheelPickerDelegate<T> implements WheelPickerDelegate {

    // The delegator
    protected WheelPicker mDelegator;

    // The context
    protected Context mContext;

    // Picker views.
    protected List<WheelView> mWheelViews;

    // Picker adapter.
    protected PickerAdapter mAdapter;

    // Callbacks
    protected WheelPicker.OnItemSelectedListener mOnItemSelectedListener;
    protected WheelPicker.OnDataChangedListener mOnDataChangedListener;

    public AbstractWheelPickerDelegate(WheelPicker delegator, Context context) {
        mDelegator = delegator;
        mContext = context;
    }

    public void setOnItemClickListener(){}

    public void setOnItemSelectedListener(){}

    @Override
    public void setOnDataChangedListener(WheelPicker.OnDataChangedListener callback) {
        mOnDataChangedListener = callback;
    }

    @Override
    public void setAdapter(PickerAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void setOnItemSelectedListener(WheelPicker.OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    @Override
    public void onLayout() {
        int sum = 0;
        for (int i = 0; i < mWheelViews.size(); i++) {
            sum += mWheelViews.get(i).getMaxItemWidth();
        }

        int base = 0;
        for (int i = 0; i < mWheelViews.size(); i++) {
            int width = mWheelViews.get(i).getMaxItemWidth();
            mWheelViews.get(i).setCameraOffsetX((int) (((base + width / 2) - sum / 2) * 0.5));
            base += width;
        }
    }

    /**
     * Class for managing state storing/restoring.
     */
    static class SavedState extends View.BaseSavedState {
        private final String mSelectedData;
        private final int mCurrentView;
        private final int mListPosition;
        private final int mListPositionOffset;

        public SavedState(Parcelable superState, String selectedData) {
            this(superState, selectedData, 0, 0, 0);
        }

        /**
         * Constructor called from {@link WheelPicker#onSaveInstanceState()}
         */
        public SavedState(Parcelable superState, String selectedData, int currentView, int listPosition, int listPositionOffset) {
            super(superState);
            mSelectedData = selectedData;
            mCurrentView = currentView;
            mListPosition = listPosition;
            mListPositionOffset = listPositionOffset;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mSelectedData = in.readString();
            mCurrentView = in.readInt();
            mListPosition = in.readInt();
            mListPositionOffset = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(mSelectedData);
            dest.writeInt(mCurrentView);
            dest.writeInt(mListPosition);
            dest.writeInt(mListPositionOffset);
        }

        public String getSelectedData() {
            return mSelectedData;
        }

        public int getCurrentView() {
            return mCurrentView;
        }

        public int getListPosition() {
            return mListPosition;
        }

        public int getListPositionOffset() {
            return mListPositionOffset;
        }

        @SuppressWarnings("all")
        // suppress unused and hiding
        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
