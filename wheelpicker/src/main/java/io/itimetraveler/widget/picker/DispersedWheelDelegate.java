package io.itimetraveler.widget.picker;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import io.itimetraveler.widget.adapter.WheelAdapter;
import io.itimetraveler.widget.model.IPickerData;
import io.itimetraveler.widget.view.AbsWheelView;
import io.itimetraveler.widget.view.WheelView;


/**
 * A delegate for Dispersed wheel picker.
 */
class DispersedWheelDelegate extends AbstractWheelPickerDelegate {

    private Context mContext;

    // Top-level container.
    private ViewGroup mContainer;

    public DispersedWheelDelegate(WheelPicker delegator, Context context) {
        super(delegator, context);
        mContext = context;

        // Set up and attach container.
        mContainer = new LinearLayout(context);
        ((LinearLayout) mContainer).setGravity(LinearLayout.HORIZONTAL);
        mContainer.setSaveFromParentEnabled(false);
        mDelegator.addView(mContainer);
    }

    @Override
    public <D extends IPickerData> void setDataSource(List<D>[] dataArray) {
        mContainer.removeAllViews();
        mWheelViews = new LinkedList<WheelView>();

        for (int j = 0; j < dataArray.length; j++) {
            List<D> dataSet = dataArray[j];
            DataAdapter adapter = new DataAdapter();
            adapter.setPickerDataList(dataSet);

            WheelView wheelView = new WheelView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    AbsWheelView.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            wheelView.setLayoutParams(lp);
            wheelView.setAdapter(adapter);
            wheelView.setSelectItem(0);

            mWheelViews.add(wheelView);
            mContainer.addView(wheelView);
//            mDelegator.addView(wheelView);
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

    private class DataAdapter<T extends IPickerData> extends WheelAdapter {
        private List<T> mIPickerDataList;

        DataAdapter(){}

        void setPickerDataList(List<T> list){
            mIPickerDataList = list;
        }

        @Override
        public int getCount() {
            return (mIPickerDataList != null) ? mIPickerDataList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return (mIPickerDataList != null) ? mIPickerDataList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                mIPickerDataList.get(position).onBindView(parent, convertView, position);
            } else {
                convertView = mIPickerDataList.get(position).onCreateView(parent);
            }
            return convertView;
        }
    }
}
