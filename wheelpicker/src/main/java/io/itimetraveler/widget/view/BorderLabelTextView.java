package io.itimetraveler.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import io.itimetraveler.widget.utils.DisplayUtil;

/**
 * Created by iTimeTraveler on 2019/3/23.
 */
public class BorderLabelTextView extends androidx.appcompat.widget.AppCompatTextView {

    private static final int BACKGROUND_COLOR_MASK = 0x00FFFFFF;

    private int strokeWidth;
    private int borderColor;
    private Paint paint = new Paint();

    public BorderLabelTextView(Context context, int borderColor, int backgroundColor) {
        super(context);
        setBackgroundColor(backgroundColor);

        int bgColor = 0;
        Drawable mBackground = getBackground();
        if(mBackground instanceof ColorDrawable){
            bgColor = ((ColorDrawable) mBackground).getColor() & BACKGROUND_COLOR_MASK;
        }
        this.borderColor = borderColor == 0 ? (0x28000000 | (~bgColor & BACKGROUND_COLOR_MASK)) : (0x99FFFFFF & borderColor);
        strokeWidth = DisplayUtil.dip2px(context, 1);

        paint.setColor(this.borderColor);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Top Border
        canvas.drawLine(0, 0, this.getWidth(), 0, paint);
        // Left
        // canvas.drawLine(0, 0, 0, this.getHeight() - strokeWidth, paint);
        // Right
        // canvas.drawLine(this.getWidth() - strokeWidth, 0, this.getWidth() - strokeWidth, this.getHeight() - strokeWidth, paint);
        // Bottom
        canvas.drawLine(0, this.getHeight(), this.getWidth(), this.getHeight(), paint);
        super.onDraw(canvas);
    }
}