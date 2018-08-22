package io.itimetraveler.widget.adapter;

import android.view.View;

import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2018/8/12.
 */

public abstract class PickerAdapter implements PickerBaseAdapter {

    /**
     * 多少列
     * @param wheelPicker
     */
    public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
        return 0;
    }

    /**
     * 某列有多少行
     * @param component
     */
    public int numberOfRowsInComponent(int component) {
        return 0;
    }

    /**
     * 某列宽度
     * @param component
     */
    public int widthForComponent(int component) {
        return 0;
    }

    /**
     * 某列行高
     * @param component
     */
    public int rowHeightForComponent(int component) {
        return 0;
    }

    /**
     * 每行的显示内容
     * @param row
     * @param component
     */
    public String titleForRow(int row, int component) {
        return null;
    }

    /**
     * 每行的显示View
     * @param row
     * @param component
     */
    public View viewForRow(int row, int component) {
        return null;
    }

}
