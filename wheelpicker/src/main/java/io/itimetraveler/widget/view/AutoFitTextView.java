package io.itimetraveler.widget.view;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

/**
 * 自定义TextView，文本内容自动调整字体大小以适应TextView的大小
 */
public class AutoFitTextView extends androidx.appcompat.widget.AppCompatTextView {

    private float mInitTextSize;
    private AutoSizeTextType autoSizeTextType = AutoSizeTextType.ZOOM;

    public AutoFitTextView(Context context) {
        this(context, null);
    }

    public AutoFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setMaxLines(1);
        this.setGravity(Gravity.CENTER);
    }

    public void setAutoSizeTextType(AutoSizeTextType type) {
        autoSizeTextType = type;
    }

    public void setText(CharSequence text, int parentWidth) {
        switch (autoSizeTextType) {
            case ZOOM:
                setText(text);
                refitText(text.toString(), parentWidth);
                break;
            case NONE:
            default:
                if (parentWidth > 0) {
                    setMaxWidth(parentWidth);
                    setEllipsize(TextUtils.TruncateAt.END);
                    setText(text);
                }
                break;
        }
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mInitTextSize = this.getTextSize();		//这个返回的单位为px
    }

    /**
     * Re size the font so the specified text fits in the text box assuming the
     * text box is the specified width.
     *
     * @param text
     * @param textViewWidth
     */
    private void refitText(String text, int textViewWidth) {
        if (textViewWidth > 0) {
            float size = mInitTextSize;	//这个返回的单位为px
            Paint paint = new Paint();
            paint.set(this.getPaint());

            int drawWidth = 0;
            Drawable[] draws = getCompoundDrawables();
            for (int i = 0; i < draws.length; i++) {
                if(draws[i]!= null){
                    drawWidth += draws[i].getBounds().width();
                }
            }
            // 获得当前TextView的有效宽度
            int availableWidth = textViewWidth - this.getPaddingLeft() - this.getPaddingRight() - getCompoundDrawablePadding() - drawWidth;
            // 所有字符所占像素宽度
            while(getTextLength(paint, size, text) > availableWidth){
                paint.setTextSize(--size);
            }
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);	//这里设置单位为 px
        }
    }

    /**
     * @return 字符串所占像素宽度
     */
    private float getTextLength(Paint paint, float textSize, String text){
        paint.setTextSize(textSize);
        return paint.measureText(text);
    }

    // 文字
    public enum AutoSizeTextType {
        ZOOM, NONE
    }
}
