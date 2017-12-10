package view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import adapter.WheelAdapter;

/**
 * Created by iTimeTraveler on 2017/12/8.
 */
public class WheelView extends View {

	private static final int SHOW_COUNT = 7;

	//半径
	private int mRadius;
	//item高度
	private int mMaxItemHeight;
	//item夹角
	private int mItemAngle = 180 / (SHOW_COUNT - 1);

	private WheelAdapter mAdapter;
	private DataSetObserver mDataSetObserver;

	private Paint mBmpPaint;
	private Camera mCamera = new Camera();
	private Matrix mMatrix = new Matrix();

	private GestureDetector mGestureDetector;
	private Scroller mScroller;


	/**
	 * Constructor
	 */
	public WheelView(Context context) {
		super(context);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initData(context);
	}

	private void initData(Context context){
		mBmpPaint = new Paint();
		mScroller = new Scroller(context);
		mGestureDetector = new GestureDetector(context, mOnGestureListener);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		getMaxItemHeight();
		int radius = calculateRadius(mMaxItemHeight);
		int diameter = radius << 1;

		//适配wrap_content
		if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
			widthSize = 200;
			heightSize = diameter;
		}else if(widthMode == MeasureSpec.AT_MOST){
			widthSize = 200;
		}else if(heightMode == MeasureSpec.AT_MOST){
			heightSize = diameter;
		}
		setMeasuredDimension(widthSize, heightSize + 500);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(mAdapter != null){
			int count = mAdapter.getCount();
			for(int i = 0; i < SHOW_COUNT - 1; i++){
				View itemView = mAdapter.getView(i);
				drawItem(canvas, itemView, i);
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mAdapter == null) return false;
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void setAdapter(WheelAdapter adapter){
		if(mAdapter != null && mDataSetObserver != null){
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		mAdapter = adapter;
		invalidate();
	}

	/**
	 * 绘制单个item
	 */
	private void drawItem(Canvas canvas, View itemView, int index){
		int currentIdx = (SHOW_COUNT - 2) / 2;
		int degree = mItemAngle * (currentIdx - index);
		int offsetY = calculateOffsetY(index);
		Bitmap bmp = convertViewToBitmap(itemView);

		if(bmp != null){
			mCamera.save();
			//绕X轴翻转
			mCamera.rotateX(degree);
			mCamera.getMatrix(mMatrix);
			mCamera.restore();
			mMatrix.preTranslate(- bmp.getWidth() / 2, - bmp.getHeight() / 2);
			mMatrix.postTranslate(bmp.getWidth() / 2, bmp.getHeight() / 2 + offsetY);
//			mMatrix.setTranslate(0, offsetY);

			canvas.save();
			canvas.drawBitmap(bmp, mMatrix, mBmpPaint);
			canvas.restore();
		}
	}

	/**
	 * 遍历得到所有子View中的最大高度
	 */
	private int getMaxItemHeight(){
		int maxHeight = 0;
		if(mAdapter != null){
			for(int i = 0; i < mAdapter.getCount(); i++){
				View v = mAdapter.getView(i);
				v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				maxHeight = Math.max(maxHeight, v.getMeasuredHeight());
			}
		}
		return (mMaxItemHeight = maxHeight);
	}

	/**
	 * 计算竖直偏移
	 * @param index
	 */
	private int calculateOffsetY(int index){
		if(index < 0 || index >= SHOW_COUNT){
			return 0;
		}
		double angle = (index + 0.5) * mItemAngle * Math.PI / 180;
		int offset = (int) (mRadius * (1 - Math.cos(angle)));
		return offset;
	}

	/**
	 * 根据item高度计算半径
	 * @param itemHeight
	 */
	private int calculateRadius(int itemHeight){
		double sinHalfAngle = Math.sin(Math.PI * mItemAngle / 360);
		return mRadius = (int) Math.ceil((itemHeight >> 1) / sinHalfAngle);
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

	private GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

		@Override
		public boolean onDown(MotionEvent e) {
			return super.onDown(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	};
}
