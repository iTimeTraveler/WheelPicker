package io.itimetraveler.widget.picker;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.itimetraveler.widget.view.AbsWheelView;
import io.itimetraveler.widget.view.TextWheelView;
import io.itimetraveler.widget.view.WheelView;


/**
 * A delegate for picking up a set of texts.
 */
class DisperseWheelDelegate extends WheelPicker.AbstractWheelPickerDelegate {

    private Context mContext;

    // Top-level container.
    private ViewGroup mContainer;

    // Accessibility strings.
    private String mSelectText;

    public DisperseWheelDelegate(WheelPicker delegator, Context context) {
        super(delegator, context);
        mContext = context;

        // Set up and attach container.
        mContainer = new LinearLayout(context);
        ((LinearLayout) mContainer).setGravity(LinearLayout.HORIZONTAL);
        mContainer.setSaveFromParentEnabled(false);
        mDelegator.addView(mContainer);
    }

    @Override
    public void init(final Collection[] dataArray, WheelPicker.OnDataChangedListener onDataChangedListener) {
//        mContainer.removeAllViews();
//        mWheelViews = new LinkedList<WheelView>();
//
//        for (int j = 0; j < dataArray.length; j++) {
//            Collection dataSet = dataArray[j];
//            TextWheelView textWheelView = new TextWheelView(mContext);
//            textWheelView.setTextList((List<String>) dataSet);
//
//            AbsWheelView.OnItemSelectedListener listener = new AbsWheelView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AbsWheelView parentView, int position) {
//                    int i = mWheelViews.indexOf(parentView);
//
//                    Collection dataSet;
//                    if (i >= 0 && i < mWheelViews.size()) {
//                        switch (i) {
//                            case 0:
//                                dataSet = ((List) dataArray[0]).get(position);
//                                break;
//                            case 1:
//                                break;
//                            case 2:
//                                break;
//                        }
//                        ((TextWheelView) mWheelViews.get(i)).setTextList(dataSet);
//                    }
//                }
//            };
//            textWheelView.setOnItemSelectedListener(listener);
//            mWheelViews.add(textWheelView);
//            mContainer.addView(textWheelView);
//        }
    }

    @Override
    public void setDataSource(final List[] dataArray) {
        mContainer.removeAllViews();
        mWheelViews = new LinkedList<WheelView>();

        for (int j = 0; j < dataArray.length; j++) {
            List dataSet = dataArray[j];
            TextWheelView textWheelView = new TextWheelView(mContext);

            switch (j) {
                case 0:
                    textWheelView.setTextList((List<String>) dataSet);
                    break;
                case 1:
                    textWheelView.setTextList((List<String>) dataSet.get(0));
                    break;
                case 2:
                    textWheelView.setTextList(((List<List<String>>) dataSet.get(0)).get(0));
                    break;
            }

            AbsWheelView.OnItemSelectedListener listener = new AbsWheelView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AbsWheelView parentView, int position) {
                    int i = mWheelViews.indexOf(parentView);

                    List dataSet = null;
                    if (i >= 0 && i < mWheelViews.size() - 1) {
                        switch (i) {
                            case 0:
                                dataSet = (List) dataArray[1].get(position);
                                break;
                            case 1:
                                dataSet = (List) ((List) dataArray[2].get(((TextWheelView) mWheelViews.get(0)).getCurrentItemIndex())).get(position);
                                break;
                        }
                        ((TextWheelView) mWheelViews.get(i+1)).setTextList(dataSet);
                    }
                }
            };
            textWheelView.setOnItemSelectedListener(listener);
            mWheelViews.add(textWheelView);
            mContainer.addView(textWheelView);
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
