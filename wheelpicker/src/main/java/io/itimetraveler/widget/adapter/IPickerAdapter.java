package io.itimetraveler.widget.adapter;

import android.view.View;
import android.view.ViewGroup;

import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2018/8/11.
 */

interface IPickerAdapter {

    /**
     * 多少列
     * @param wheelPicker
     */
    int numberOfComponentsInWheelPicker(WheelPicker wheelPicker);

    /**
     * 某列有多少行
     * @param component
     */
    int numberOfRowsInComponent(int component);

    /**
     * 某列宽度
     * @param component
     */
    int widthForComponent(int component);

    /**
     * 某列行高
     * @param component
     */
    int rowHeightForComponent(int component);

    /**
     * 每行的显示View
     * @param row
     * @param component
     */
    View onCreateView(ViewGroup parent, int row, int component);

    /**
     * 每行的显示View，但已有回收复用的View
     * @param row
     * @param component
     */
    void onBindView(ViewGroup parent, View convertView, int row, int component);

    String labelOfComponent(int component);
}
