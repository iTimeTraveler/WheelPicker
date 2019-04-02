package io.itimetraveler.widget.picker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.adapter.WheelAdapter;
import io.itimetraveler.widget.view.AbsWheelView;
import io.itimetraveler.widget.view.BorderLabelTextView;
import io.itimetraveler.widget.view.WheelView;

/**
 * An abstract class which can be used as a start for WheelPicker implementations
 */
abstract class AbstractWheelPickerDelegate implements IWheelPickerDelegate {

    // The delegator
    protected WheelPicker mDelegator;

    // The context
    protected Context mContext;

    // Top-level container.
    private ViewGroup mContainer;

    // Picker views.
    protected List<WheelView> mWheelViews = new LinkedList<WheelView>();

    // Picker adapter.
    protected PickerAdapter mPickerAdapter;

    // Picker options.
    protected PicketOptions mPicketOptions;

    // Callbacks
    protected WheelPicker.OnItemSelectedListener mOnItemSelectedListener;
    protected WheelPicker.OnDataChangedListener mOnDataChangedListener;

    protected int[] mSelectedPositions;

    public AbstractWheelPickerDelegate(WheelPicker delegator, Context context, PicketOptions options) {
        mDelegator = delegator;
        mContext = context;
        mPicketOptions = options;

        // Set up and attach container.
        mContainer = new LinearLayout(context);
        ((LinearLayout) mContainer).setGravity(Gravity.CENTER);
        mContainer.setSaveFromParentEnabled(false);
        mDelegator.addView(mContainer);
    }

    public void setOnItemClickListener(){}

    public void setOnItemSelectedListener(){}

    @Override
    public void setOnDataChangedListener(WheelPicker.OnDataChangedListener callback) {
        mOnDataChangedListener = callback;
    }

    @Override
    public void setAdapter(PickerAdapter pickerAdapter) {
        mPickerAdapter = pickerAdapter;
        mContainer.removeAllViews();
        mWheelViews = new LinkedList<WheelView>();

        int count = mPickerAdapter.numberOfComponentsInWheelPicker(mDelegator);
        mSelectedPositions = new int[count];

        // 空白填充
        fillGapWithBlackLine(mContainer);

        // 创建用户指定数量的 WheelView 组件
        for (int j = 0; j < count; j++) {
            DataAdapter adapter = new DataAdapter(mPickerAdapter, j);

            WheelView wheelView = new WheelView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    mPicketOptions.isDividedEqually() ? 0 : AbsWheelView.LayoutParams.WRAP_CONTENT,
                    AbsWheelView.LayoutParams.WRAP_CONTENT);
            if (mPicketOptions.isDividedEqually()) {
                lp.weight = 1;
            }
            wheelView.setDividerColor(mPicketOptions.getDividerColor());
            wheelView.setBackgroundColor(mPicketOptions.getBackgroundColor());
            wheelView.setLayoutParams(lp);
            wheelView.setAdapter(adapter);
            wheelView.setSelection(0);

            mWheelViews.add(wheelView);
            mContainer.addView(wheelView);

            // 标签或单位
            String label;
            if (!TextUtils.isEmpty(label = mPickerAdapter.labelOfComponent(j))) {
                LinearLayout.LayoutParams lvlp = new LinearLayout.LayoutParams(
                        AbsWheelView.LayoutParams.WRAP_CONTENT,
                        AbsWheelView.LayoutParams.WRAP_CONTENT);
                BorderLabelTextView labelView = new BorderLabelTextView(mContext,
                        mPicketOptions.getDividerColor(),
                        mPicketOptions.getBackgroundColor());
                labelView.setPadding(20, 4, 20, 4);
                labelView.setTextSize(20);
                labelView.setTextColor(PicketOptions.SELECTED_TEXT_COLOR);

                labelView.setGravity(Gravity.CENTER_VERTICAL);
                labelView.setLayoutParams(lvlp);
                labelView.setText(label);
                mContainer.addView(labelView);
            }

            final int componentIdx = j;
            wheelView.setOnItemSelectedListener(new AbsWheelView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AbsWheelView parentView, int position) {
                    mSelectedPositions[componentIdx] = position;
                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onItemSelected(mDelegator, mSelectedPositions);
                    }
                    onPickerItemSelected(parentView, position, componentIdx);
                }
            });
        }

        // 空白填充
        fillGapWithBlackLine(mContainer);

        mContainer.setBackgroundColor(mPicketOptions.getBackgroundColor());
    }

    abstract void onPickerItemSelected(AbsWheelView parentView, int row, int componentIdx);

    @Override
    public void setOnItemSelectedListener(WheelPicker.OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    @Override
    public void setSelection(int component, int row) {
        if (component >= 0 && component < mWheelViews.size()) {
            WheelView wheelView = mWheelViews.get(component);
            if (row >= 0 && row < wheelView.getCount()) {
                wheelView.setSelection(row);
            }
        }
    }

    @Override
    public int[] getSelectedPositions() {
        return mSelectedPositions;
    }

    @Override
    public void onLayout() {
        if (mWheelViews == null) return;

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
     * 使用选中线填充指示器的左右空白处
     * @param viewGroup
     */
    private void fillGapWithBlackLine(ViewGroup viewGroup) {
        // 空白填充
        LinearLayout.LayoutParams lvlp = new LinearLayout.LayoutParams(AbsWheelView.LayoutParams.WRAP_CONTENT,
                AbsWheelView.LayoutParams.WRAP_CONTENT);
        // 滚轮控件填充不满时，空白强制左右延伸到填满
        if (!mPicketOptions.isDividedEqually()) {
            lvlp.weight = 1;
        }
        BorderLabelTextView labelView = new BorderLabelTextView(mContext,
                mPicketOptions.getDividerColor(),
                mPicketOptions.getBackgroundColor());
        labelView.setPadding(0, 4, 0, 4);
        labelView.setTextSize(20);
        labelView.setTextColor(PicketOptions.SELECTED_TEXT_COLOR);

        labelView.setGravity(Gravity.CENTER_VERTICAL);
        labelView.setLayoutParams(lvlp);
        labelView.setText(" ");
        viewGroup.addView(labelView);
    }

    /**
     * 用于适配每个WheelView的适配器
     */
    private class DataAdapter extends WheelAdapter {
        private PickerAdapter mPickerAdapter;
        private int mIndexOfComponent;

        DataAdapter(PickerAdapter pickerAdapter, int indexOfComponent){
            this.mPickerAdapter = pickerAdapter;
            this.mIndexOfComponent = indexOfComponent;
        }

        @Override
        public int getCount() {
            return mPickerAdapter.numberOfRowsInComponent(mIndexOfComponent);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                mPickerAdapter.onBindView(parent, convertView, position, mIndexOfComponent);
            } else {
                convertView = mPickerAdapter.onCreateView(parent, position, mIndexOfComponent);
            }
            return convertView;
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
