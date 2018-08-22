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

    /**
     * The callback used to indicate the user changed the data.
     */
    public interface OnDataChangedListener {
        void onDataChanged(WheelPicker view, String data);
    }

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

    public void init(Collection[] dataArray, WheelPicker.OnDataChangedListener onDataChangedListener) {
        mDelegate.init(dataArray, onDataChangedListener);
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
     * Set the listener that will receive notifications when the wheel view
     * finishes scrolling and select an option automatically.
     * @param l the item selected listener
     */
    public void setOnItemSelectedListener(OnItemSelectedListener l) {
        mDelegate.setOnItemSelectedListener(l);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mDelegate.onLayout();
        super.onLayout(changed, left, top, right, bottom);
    }

    public interface OnItemSelectedListener {

        /**
         * Callback method to be invoked while the wheel view's item is being selected.
         * @param parentView
         * @param position
         */
        public void onItemSelected(AbsWheelView parentView, int position);
    }

    /**
     * A delegate interface that defined the public API of the WheelPicker. Allows different
     * WheelPicker implementations. This would need to be implemented by the WheelPicker delegates
     * for the real behavior.
     *
     * @hide
     */
    interface WheelPickerDelegate<T> {
        void init(Collection<T>[] dataArray, OnDataChangedListener onDataChangedListener);

        void setOnDataChangedListener(OnDataChangedListener onDataChangedListener);

        void setDataSource(List<T>... item1);

        void setAdapter(PickerAdapter adapter);

        void setOnItemSelectedListener(OnItemSelectedListener listener);

        void onLayout();

        void setEnabled(boolean enabled);
        boolean isEnabled();

        void onConfigurationChanged(Configuration newConfig);

        Parcelable onSaveInstanceState(Parcelable superState);
        void onRestoreInstanceState(Parcelable state);
    }

    /**
     * An abstract class which can be used as a start for WheelPicker implementations
     */
    abstract static class AbstractWheelPickerDelegate<T> implements WheelPickerDelegate {
        // The delegator
        protected WheelPicker mDelegator;

        // The context
        protected Context mContext;

        // Picker views.
        protected List<WheelView> mWheelViews;

        // Picker adapter.
        protected PickerAdapter mAdapter;

        // Callbacks
        protected OnItemSelectedListener mOnItemSelectedListener;
        protected OnDataChangedListener mOnDataChangedListener;

        public AbstractWheelPickerDelegate(WheelPicker delegator, Context context) {
            mDelegator = delegator;
            mContext = context;
        }

        public void setOnItemClickListener(){}

        public void setOnItemSelectedListener(){}

        @Override
        public void setOnDataChangedListener(OnDataChangedListener callback) {
            mOnDataChangedListener = callback;
        }

        @Override
        public void setAdapter(PickerAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void setOnItemSelectedListener(OnItemSelectedListener listener) {
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
}
