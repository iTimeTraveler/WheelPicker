package io.itimetraveler.widget.adapter;

import android.view.View;

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
     * 每行的显示内容
     * @param row
     * @param component
     */
    String titleForRow(int row, int component);

    /**
     * 每行的显示View
     * @param row
     * @param component
     */
    View viewForRow(int row, int component);
}
