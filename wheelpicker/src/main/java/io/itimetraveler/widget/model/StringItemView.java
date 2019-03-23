package io.itimetraveler.widget.model;

import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import io.itimetraveler.widget.picker.PicketOptions;
import io.itimetraveler.widget.view.AutoFitTextView;

/**
 * Created by iTimeTraveler on 2018/9/14.
 */
public class StringItemView extends BasePickerItemView<String> {

    public StringItemView(String data) {
        super(data);
        this.data = data;
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        AutoFitTextView tv = new AutoFitTextView(parent.getContext());
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(20, 3, 20, 3);
        tv.setTextSize(PicketOptions.DEFAULT_TEXT_SIZE);

        //选中颜色
        int[] colors = new int[] {PicketOptions.SELECTED_TEXT_COLOR, PicketOptions.DEFAULT_TEXT_COLOR};
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
        return "StringItemView{" +
                ", data='" + data + '\'' +
                '}';
    }
}
