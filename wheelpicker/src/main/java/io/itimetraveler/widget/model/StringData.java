package io.itimetraveler.widget.model;

import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import io.itimetraveler.widget.view.AutoFitTextView;

/**
 * Created by iTimeTraveler on 2018/9/14.
 */
public class StringData implements IPickerData {

    //默认配置
    private int mTextSize = 20;
    private int mDefaultColor = 0xFFAAAAAA;
    private int mSelectColor = 0xFF333333;

    private String data;

    public StringData(String data) {
        this.data = data;
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        AutoFitTextView tv = new AutoFitTextView(parent.getContext());
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(20, 3, 20, 3);
        tv.setTextSize(mTextSize);

        //选中颜色
        int[] colors = new int[] {mSelectColor, mDefaultColor};
        int[][] states = {{android.R.attr.state_selected}, {}};
        tv.setTextColor(new ColorStateList(states, colors));
        tv.setText(data, parent.getMeasuredWidth());
        return tv;
    }

    @Override
    public void onBindView(ViewGroup parent, View view, int position) {
        if (view instanceof AutoFitTextView) {
            AutoFitTextView textView = ((AutoFitTextView) view);
            textView.setText(data, parent.getMeasuredWidth());
        }
    }

    @Override
    public String toString() {
        return "StringData{" +
                ", data='" + data + '\'' +
                '}';
    }
}
