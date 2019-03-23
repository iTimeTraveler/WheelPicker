package io.itimetraveler.widget.pickerselector;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by iTimeTraveler on 2019/3/22.
 */
public class CountDownWheelPicker extends DigitalCipherPicker {

    private int maxCount;

    public CountDownWheelPicker(Context context) {
        this(context, null);
    }

    public CountDownWheelPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownWheelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        setRows(maxCount);
    }

    public void show() {
        setCipherCount(1);
        setEnabled(false);
        setSelection(0, maxCount - 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = maxCount; i > 0; i--) {
                    try {
                        Thread.sleep(1000);
                        CountDownWheelPicker.this.setSelection(0, i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }


}
