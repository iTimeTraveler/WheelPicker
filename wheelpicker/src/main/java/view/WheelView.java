package view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import adapter.WheelAdapter;

/**
 * Created by iTimeTraveler on 2017/12/8.
 */
public class WheelView extends View {

	private WheelAdapter mAdapter;
	private DataSetObserver mDataSetObserver;

	private Paint mBmpPaint;


	/**
	 * Constructor
	 */
	public WheelView(Context context) {
		super(context);
		initPaint();
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
	}

	private void initPaint(){
		mBmpPaint = new Paint();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		//适配wrap_content
		if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
			widthSize = 200;
			heightSize = 200;
		}else if(widthMode == MeasureSpec.AT_MOST){
			widthSize = 200;
		}else if(heightMode == MeasureSpec.AT_MOST){
			heightSize = 200;
		}
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(mAdapter != null){
			int offsetY = 0;
			int count = mAdapter.getCount();
			for(int i = 0; i < count; i++){
				View itemView = mAdapter.getView(i);
				Bitmap bmp = convertViewToBitmap(itemView);
				if(bmp != null){
					canvas.drawBitmap(bmp, 0, offsetY, mBmpPaint);
					offsetY += bmp.getHeight();
				}
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void setAdapter(WheelAdapter adapter){
		if(mAdapter != null && mDataSetObserver != null){
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		mAdapter = adapter;
		invalidate();
	}

	/**
	 * View转换为Bitmap
	 * @param view
	 */
	private Bitmap convertViewToBitmap(View view){
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		return view.getDrawingCache();
	}
}
