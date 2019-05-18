package io.itimetraveler.widget.pickerselector;

import android.content.Context;

/**
 * Created by iTimeTraveler on 2019/3/22.
 */
public class CountDownWheelPicker extends DigitalCipherPicker {

    private static final int DEFAULT_ROW_COUNT = 10;

    private int maxCount = DEFAULT_ROW_COUNT;

    public CountDownWheelPicker(Context context) {
        this(context, DEFAULT_ROW_COUNT);
    }

    public CountDownWheelPicker(Context context, int maxCount) {
        super(context, 1, maxCount);
        this.maxCount = maxCount;
        init0();
    }

    private void init0() {
        setEnabled(false);
        setSelection(0, maxCount - 1);
    }

    public void startCountDown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = maxCount - 1; i >= 0; i--) {
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
