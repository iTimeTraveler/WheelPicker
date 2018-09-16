package io.itimetraveler.widget.picker;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.itimetraveler.widget.adapter.WheelAdapter;
import io.itimetraveler.widget.model.PickerNode;
import io.itimetraveler.widget.view.AbsWheelView;
import io.itimetraveler.widget.view.WheelView;

/**
 * A delegate for linked wheel picker.
 */
public class LinkedWheelDelegate extends AbstractWheelPickerDelegate{

    private Context mContext;

    // Top-level container.
    private ViewGroup mContainer;

    private List<Integer> mIndexes;

    public LinkedWheelDelegate(WheelPicker delegator, Context context) {
        super(delegator, context);
        mContext = context;

        // Set up and attach container.
        mContainer = new LinearLayout(context);
        ((LinearLayout) mContainer).setGravity(LinearLayout.HORIZONTAL);
        mContainer.setSaveFromParentEnabled(false);
        mDelegator.addView(mContainer);
    }

    @Override
    public <N extends PickerNode> void setDataSource(final List<N> nodeList) {
        mContainer.removeAllViews();
        mWheelViews = new LinkedList<WheelView>();
        mIndexes = new ArrayList<>();

        List<N> list = nodeList;
        while (list != null && list.size() > 0) {
            DataAdapter adapter = new DataAdapter();
            adapter.setPickerNodeList(list);

            WheelView wheelView = new WheelView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    AbsWheelView.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            wheelView.setLayoutParams(lp);
            wheelView.setAdapter(adapter);
            wheelView.setSelectItem(0);

            mWheelViews.add(wheelView);
            mIndexes.add(0);
            mContainer.addView(wheelView);
//            mDelegator.addView(wheelView);

            list = list.get(0).getNextLevel();
        }

        for (int i = 0; i < mWheelViews.size(); i++) {
            final int idx = i;
            AbsWheelView.OnItemSelectedListener listener = new AbsWheelView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AbsWheelView parentView, int position) {
                    mIndexes.set(idx, position);
                    if (idx < mWheelViews.size() - 1) {

                        List<N> list = nodeList;
                        for (int n = 0; n < idx; n++) {
                            list = list.get(mIndexes.get(n)).getNextLevel();
                        }
                        ((DataAdapter) (mWheelViews.get(idx+1).getAdapter())).setPickerNodeList(list.get(position).getNextLevel());
                    }
                }
            };
            mWheelViews.get(i).setOnItemSelectedListener(listener);
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


    private class DataAdapter<N extends PickerNode> extends WheelAdapter {
        private List<N> mPickerNodeList;

        DataAdapter(){}

        void setPickerNodeList(List<N> list){
            mPickerNodeList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return (mPickerNodeList != null) ? mPickerNodeList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return (mPickerNodeList != null) ? mPickerNodeList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                mPickerNodeList.get(position).getData().onBindView(parent, convertView, position);
            } else {
                convertView = mPickerNodeList.get(position).getData().onCreateView(parent);
            }
            return convertView;
        }
    }
}
