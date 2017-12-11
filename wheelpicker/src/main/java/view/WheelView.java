package view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import adapter.WheelAdapter;

/**
 * Created by iTimeTraveler on 2017/12/8.
 */
public class WheelView extends AbsWheelView {

	private static final int SHOW_COUNT = 7;
	private static final float MIN_DEGREE_DELTA_DISTANCE = 1f;

	//半径
	private int mRadius;
	//item高度
	private int mMaxItemHeight;
	private int mMaxItemWidth;
	//item夹角
	private int mItemAngle = 180 / (SHOW_COUNT - 1);

	private WheelAdapter mAdapter;
	private DataSetObserver mDataSetObserver;

	private Camera mCamera = new Camera();
	private Matrix mMatrix = new Matrix();
	private int mDegree;

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

		measureMaxItemWidthHeight();
		int diameter = (calculateRadius(mMaxItemHeight)) << 1;

		//适配wrap_content
		if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
			widthSize = mMaxItemWidth;
			heightSize = diameter;
		}else if(widthMode == MeasureSpec.AT_MOST){
			widthSize = mMaxItemWidth;
		}else if(heightMode == MeasureSpec.AT_MOST){
			heightSize = diameter;
		}
		setMeasuredDimension(widthSize + getPaddingLeft() + getPaddingRight(),
				heightSize + getPaddingTop() + getPaddingBottom() + 500);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(mAdapter != null){
			float offsetY = 0;
			int count = mAdapter.getCount();
			for(int i = 0; i < count - 1; i++){
				View itemView = mAdapter.getView(i);

				int currentIdx = (SHOW_COUNT - 2) / 2;
				int degree = mItemAngle * (currentIdx - i) + mDegree;
				int h = drawItem(canvas, itemView, offsetY, degree);
				offsetY += h;
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

		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				mGestureDetector.onTouchEvent(event);
				break;
			case MotionEvent.ACTION_UP:
				mDegree = 0;
				break;
		}
		return true;
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
	private int drawItem(Canvas canvas, View itemView, float offsetY, int degree){
		Bitmap bmp = convertViewToBitmap(itemView);
		int height = 0;

		if(bmp != null){
			height = calculateHeightAfterRotate(degree, bmp.getHeight());

			mCamera.save();
			mCamera.translate(-mMaxItemWidth / 2, -mRadius, 0);
			//绕X轴翻转
			mCamera.rotateX(degree);
			mCamera.getMatrix(mMatrix);
			mCamera.restore();
//			mMatrix.preTranslate(- bmp.getWidth() / 2, - bmp.getHeight() / 2);
//			mMatrix.postTranslate(bmp.getWidth() / 2, bmp.getHeight() / 2);

			//使用pre将旋转中心移动到和Camera位置相同。
			mMatrix.preTranslate(- bmp.getWidth() / 2 + mMaxItemWidth / 2, - bmp.getHeight() / 2 + mRadius);
			// 使用post将图片(View)移动到原来的位置
			mMatrix.postTranslate(bmp.getWidth() / 2 - mMaxItemWidth / 2, bmp.getHeight() / 2 - mRadius);

			canvas.save();
			canvas.translate(0, offsetY);
			canvas.drawBitmap(bmp, mMatrix, null);
			//设置图片抗锯齿
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.restore();
		}

		return height;
	}

	/**
	 * 遍历得到所有子View中的最大高度
	 */
	private void measureMaxItemWidthHeight(){
		int maxWidth = 0;
		int maxHeight = 0;
		if(mAdapter != null){
			for(int i = 0; i < mAdapter.getCount(); i++){
				View v = mAdapter.getView(i);
				v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				maxWidth = Math.max(maxWidth, v.getMeasuredWidth());
				maxHeight = Math.max(maxHeight, v.getMeasuredHeight());
			}
		}
		mMaxItemWidth = maxWidth;
		mMaxItemHeight = maxHeight;
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
	 * 弯曲变换后的高度
	 * @param degree
	 * @param OldHeight
	 */
	private int calculateHeightAfterRotate(int degree, int OldHeight){
		double angle = degree * Math.PI / 180;
		return (int) (OldHeight * Math.cos(angle));
	}

	/**
	 * 根据item高度计算半径
	 * @param itemHeight
	 */
	private int calculateRadius(int itemHeight){
		mItemAngle = 180 / (SHOW_COUNT - 1);
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
			doScroll(distanceY);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	};

	private void doScroll(float deltaY){
		if(mAdapter != null){
			mDegree += (int) deltaY;
			Log.e("xwl", "deltaY:" + deltaY);
			invalidate();
		}
	}
}
