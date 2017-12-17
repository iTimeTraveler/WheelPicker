package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;
import java.util.HashMap;

import adapter.WheelAdapter;

/**
 * Created by iTimeTraveler on 2017/12/8.
 */
public class WheelView extends AbsWheelView {
	private static final String TAG = "WheelView";

	private static final int SHOW_COUNT = 11;
	private static final int NO_POSITION = -1;

	//半径
	private int mRadius;

	//每一个View对应的弯曲角度
	private HashMap<View, Integer> childrenAngleMap;

	private Camera mCamera = new Camera();
	private Matrix mMatrix = new Matrix();

	/**
	 * Constructor
	 */
	public WheelView(Context context) {
		this(context, null);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initData(context);
	}

	private void initData(Context context){
		mItemAngle = 180 / (SHOW_COUNT - 1);
		setSelectItem(0);
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


		mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
//		if (mItemCount > 0 && (widthMode == MeasureSpec.UNSPECIFIED
//				|| heightMode == MeasureSpec.UNSPECIFIED)) {
//			final View child = obtainView(0, mIsScrap);
//
//			// Lay out child directly against the parent measure spec so that
//			// we can obtain exected minimum width and height.
//			measureScrapChild(child, 0, widthMeasureSpec, heightSize);
//
//			mRecycler.addScrapView(child, 0);
//		}

		// TODO: after first layout we should maybe start at the first visible position, not 0
		measureHeightOfChildren(widthMeasureSpec, 0, NO_POSITION, heightSize, -1);

		setMeasuredDimension(widthSize, heightSize);
//		setMeasuredDimension(widthSize + getPaddingLeft() + getPaddingRight(),
//				heightSize + getPaddingTop() + getPaddingBottom());
	}

	@Override
	protected void layoutChildren() {
		if (mAdapter == null) {
			return;
		}

		mRecycler.fillActiveViews(getChildCount(), mFirstPosition);

		// Clear out old views
		detachAllViewsFromParent();

		final int childrenTop = getPaddingTop();
		final int childCount = getChildCount();
//		if (childCount == 0) {
//			fillFromTop(childrenTop);
//		}else{
			fillSpecific(mFirstPosition, childrenTop);
		Log.e("layoutChildren=====", "mScrollingDegree:"+ mScrollingDegree + ",mFirstPosition:" + mFirstPosition);
//		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int firstPos = mFirstPosition;
		if(mAdapter != null){
			int count = getChildCount();
			for(int i = 0; i < count; i++){
				View itemView = getChildAt(i);
				final int position = firstPos + i;

				int degree = (mCurrentItemIndex - position) * mItemAngle + mScrollingDegree;
				drawItem(canvas, itemView, position, degree);
			}
		}

		Paint linePaint = new Paint();
		linePaint.setColor(Color.parseColor("#990000"));
		canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, linePaint);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * Measures the height of the given range of children (inclusive) and
	 * returns the height with this ListView's padding and divider heights
	 * included. If maxHeight is provided, the measuring will stop when the
	 * current height reaches maxHeight.
	 *
	 * @param widthMeasureSpec The width measure spec to be given to a child's
	 *            {@link View#measure(int, int)}.
	 * @param startPosition The position of the first child to be shown.
	 * @param endPosition The (inclusive) position of the last child to be
	 *            shown. Specify {@link #NO_POSITION} if the last child should be
	 *            the last available child from the adapter.
	 * @param maxHeight The maximum height that will be returned (if all the
	 *            children don't fit in this value, this value will be
	 *            returned).
	 * @param disallowPartialChildPosition In general, whether the returned
	 *            height should only contain entire children. This is more
	 *            powerful--it is the first inclusive position at which partial
	 *            children will not be allowed. Example: it looks nice to have
	 *            at least 3 completely visible children, and in portrait this
	 *            will most likely fit; but in landscape there could be times
	 *            when even 2 children can not be completely shown, so a value
	 *            of 2 (remember, inclusive) would be good (assuming
	 *            startPosition is 0).
	 * @return The height of this ListView with the given children.
	 */
	final int measureHeightOfChildren(int widthMeasureSpec, int startPosition, int endPosition,
									  int maxHeight, int disallowPartialChildPosition) {
		final WheelAdapter adapter = mAdapter;
		if (adapter == null) {
			return getPaddingTop() + getPaddingBottom();
		}

		// Include the padding of the list
		int returnedHeight = getPaddingTop() + getPaddingBottom();
		final int dividerHeight = 0;
		// The previous height value that was less than maxHeight and contained
		// no partial children
		int prevHeightWithoutPartialChild = 0;
		int i;
		View child;

		// mItemCount - 1 since endPosition parameter is inclusive
		endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1 : endPosition;
		final AbsWheelView.RecycleBin recycleBin = mRecycler;
		final boolean[] isScrap = mIsScrap;

		for (i = startPosition; i <= endPosition; ++i) {
			child = obtainView(i, isScrap);

			measureScrapChild(child, i, widthMeasureSpec, maxHeight);

			if (i > 0) {
				// Count the divider for all but one child
				returnedHeight += dividerHeight;
			}

			// Recycle the view before we possibly return from the method
			recycleBin.addScrapView(child, -1);

			returnedHeight += child.getMeasuredHeight();

			if (returnedHeight >= maxHeight) {
				// We went over, figure out which height to return.  If returnedHeight > maxHeight,
				// then the i'th position did not fit completely.
				return (disallowPartialChildPosition >= 0) // Disallowing is enabled (> -1)
						&& (i > disallowPartialChildPosition) // We've past the min pos
						&& (prevHeightWithoutPartialChild > 0) // We have a prev height
						&& (returnedHeight != maxHeight) // i'th child did not fit completely
						? prevHeightWithoutPartialChild
						: maxHeight;
			}

			if ((disallowPartialChildPosition >= 0) && (i >= disallowPartialChildPosition)) {
				prevHeightWithoutPartialChild = returnedHeight;
			}
		}

		// At this point, we went through the range of children, and they each
		// completely fit, so return the returnedHeight
		return returnedHeight;
	}

	private void measureScrapChild(View child, int position, int widthMeasureSpec, int heightHint) {
		LayoutParams p = (LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = (AbsWheelView.LayoutParams) generateDefaultLayoutParams();
			child.setLayoutParams(p);
		}

		final int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
				getPaddingLeft() + getPaddingRight(), p.width);
		final int lpHeight = p.height;
		final int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(heightHint, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);

		// Since this view was measured directly aginst the parent measure
		// spec, we must measure it again before reuse.
		child.forceLayout();
	}

	/**
	 * Fills the list from top to bottom, starting with mFirstPosition
	 *
	 * @param nextTop The location where the top of the first item should be
	 *        drawn
	 */
	private void fillFromTop(int nextTop) {
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}
		fillDown(mFirstPosition, nextTop);
	}

	/**
	 * Put a specific item at a specific location on the screen and then build
	 * up and down from there.
	 *
	 * @param position The reference view to use as the starting point
	 * @param top Pixel offset from the top of this view to the top of the
	 *        reference view.
	 */
	private void fillSpecific(int position, int top) {
		View temp = makeAndAddView(position, top, true, getPaddingLeft(), false);
		// Possibly changed again in fillUp if we add rows above this one.
		mFirstPosition = position;

		final int dividerHeight = 0;
		fillUp(position - 1, temp.getTop() - dividerHeight);
		fillDown(position + 1, temp.getBottom() + dividerHeight);
//		int childCount = getChildCount();
//		if (childCount > 0) {
//			correctTooHigh(childCount);
//		}
	}

	/**
	 * Fills the list from pos up to the top of the list view.
	 *
	 * @param pos The first position to put in the list
	 * @param nextBottom The location where the bottom of the item associated
	 *        with pos should be drawn
	 */
	private void fillUp(int pos, int nextBottom){
		int end = getPaddingTop();
		while (isDegreeVisible(getDeflectionDegree(pos)) && pos >= 0) {
			// is this the selected item?
			View child = makeAndAddView(pos, nextBottom, false, getPaddingLeft(), false);

			nextBottom = child.getTop();
			pos--;
		}
		mFirstPosition = pos + 1;
	}

	/**
	 * Fills the list from pos down to the end of the list view.
	 *
	 * @param pos The first position to put in the list
	 * @param nextTop The location where the top of the item associated with pos
	 *        should be drawn
	 */
	private void fillDown(int pos, int nextTop) {
		int end = (getBottom() - getTop());
		while (isDegreeVisible(getDeflectionDegree(pos)) && pos < mItemCount) {
			// is this the selected item?
			View child = makeAndAddView(pos, nextTop, true, getPaddingLeft(), false);

			nextTop = child.getBottom();
			pos++;
		}
	}

	@Override
	void fillGap(boolean down) {
		final int count = getChildCount();
		//down表示向上滑动的操作
		if (down) {
			int paddingTop = getPaddingTop();
			final int startOffset = count > 0 ? getChildAt(count - 1).getBottom() :
					paddingTop;
			fillDown(mFirstPosition + count, startOffset);
		} else {
			int paddingBottom = getPaddingBottom();
			final int startOffset = count > 0 ? getChildAt(0).getTop() :
					getHeight() - paddingBottom;
			fillUp(mFirstPosition - 1, startOffset);
		}
	}

	/**
	 * Obtain the view and add it to our list of children. The view can be made
	 * fresh, converted from an unused view, or used as is if it was in the
	 * recycle bin.
	 *
	 * @param position Logical position in the list
	 * @param y Top or bottom edge of the view to add
	 * @param flow If flow is true, align top edge to y. If false, align bottom
	 *        edge to y.
	 * @param childrenLeft Left edge where children should be positioned
	 * @param selected Is this position selected?
	 * @return View that was added
	 */
	private View makeAndAddView(int position, int y, boolean flow, int childrenLeft,
								boolean selected) {
		View child;

		if (!mDataChanged) {
			// Try to use an existing view for this position
			child = mRecycler.getActiveView(position);
			if (child != null) {
				// Found it -- we're using an existing child
				// This just needs to be positioned
				setupChild(child, position, y, flow, childrenLeft, selected, true);

				return child;
			}
		}

		// Make a new view for this position, or convert an unused view if possible
		child = obtainView(position, mIsScrap);
		// This needs to be positioned and measured
		setupChild(child, position, y, flow, childrenLeft, selected, mIsScrap[0]);

		return child;
	}

	/**
	 * Add a view as a child and make sure it is measured (if necessary) and
	 * positioned properly.
	 *
	 * @param child The view to add
	 * @param position The position of this child
	 * @param y The y position relative to which this view will be positioned
	 * @param flowDown If true, align top edge to y. If false, align bottom
	 *        edge to y.
	 * @param childrenLeft Left edge where children should be positioned
	 * @param selected Is this position selected?
	 * @param recycled Has this view been pulled from the recycle bin? If so it
	 *        does not need to be remeasured.
	 */
	private void setupChild(View child, int position, int y, boolean flowDown, int childrenLeft,
							boolean selected, boolean recycled) {
		final boolean needToMeasure = !recycled || child.isLayoutRequested();

		// Respect layout params that are already in the view. Otherwise make some up...
		// noinspection unchecked
		AbsWheelView.LayoutParams p = (AbsWheelView.LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = new AbsWheelView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}

		if (recycled) {
			attachViewToParent(child, flowDown ? -1 : 0, p);
		} else {
			addViewInLayout(child, flowDown ? -1 : 0, p, true);
		}

		if (needToMeasure) {
			final int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
					getPaddingLeft() + getPaddingRight(), p.width);
			final int lpHeight = p.height;
			final int childHeightSpec;
			if (lpHeight > 0) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();
		final int childTop = flowDown ? y : y - h;

		if (needToMeasure) {
			final int childRight = childrenLeft + w;
			final int childBottom = childTop + h;
			child.layout(childrenLeft, childTop, childRight, childBottom);
		} else {
			child.offsetLeftAndRight(childrenLeft - child.getLeft());
			child.offsetTopAndBottom(childTop - child.getTop());
		}
	}

	/**
	 * 绘制单个item
	 */
	private int drawItem(Canvas canvas, View itemView, int position, int degree){
		Bitmap bmp = convertViewToBitmap(itemView);
		int offsetZ = calculateItemOffsetZ(degree);
		float offsetY = calculateItemOffsetY(degree);
//		int offsetY = position * mMaxItemHeight;
		int height = 0;


		if(bmp != null){
			height = calculateHeightAfterRotate(degree, bmp.getHeight());

			int gap = (mMaxItemHeight - height) >> 1;
			double temp = Math.cos(degree * Math.PI / 180);
//			int cameraGap = (int) (offsetZ * Math.cos(degree * Math.PI / 180));
			int cameraGap = 0;
			offsetY += degree > 0 ? -gap * temp : -gap;

			Log.v(TAG, "position:" + position + ", degree:" + degree + ", offsetY:" + offsetY);
			Log.v(TAG, "position:" + position + ", mRadius:" + (mRadius) + ", offsetY-mRadius:" + (offsetY - mRadius));
			Log.v(TAG, ".");

			mMatrix.reset();
			mCamera.save();
//			mCamera.translate(-mMaxItemWidth / 2, -mRadius, mCamera.getLocationZ());
			//镜头距离
			mCamera.translate(mCamera.getLocationX(), mCamera.getLocationY(), mCamera.getLocationZ() + offsetZ);
			//绕X轴翻转
			mCamera.rotateX(degree);
			mCamera.getMatrix(mMatrix);
			mCamera.restore();
			mMatrix.preTranslate(- bmp.getWidth() / 2, - bmp.getHeight() / 2);
			mMatrix.postTranslate(bmp.getWidth() / 2, bmp.getHeight() / 2);

//			//使用pre将旋转中心移动到和Camera位置相同。
//			mMatrix.preTranslate(- bmp.getWidth() / 2 + mMaxItemWidth / 2, - bmp.getHeight() / 2 + mRadius);
//			// 使用post将图片(View)移动到原来的位置
//			mMatrix.postTranslate(bmp.getWidth() / 2 - mMaxItemWidth / 2, bmp.getHeight() / 2 - mRadius);

			canvas.save();
			canvas.translate(0, offsetY);
			canvas.drawBitmap(bmp, mMatrix, null);
			//设置图片抗锯齿
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.restore();


			Paint linePaint = new Paint();
			linePaint.setColor(Color.parseColor("#00AA00"));
			canvas.drawLine(0, offsetY, getWidth(), offsetY, linePaint);

			Paint textPaint = new Paint();
			textPaint.setColor(Color.parseColor("#00AA00"));
			textPaint.setTextSize(15);
			canvas.drawText("" + position,0, offsetY, textPaint);

			Paint linePaint1 = new Paint();
			linePaint1.setColor(Color.parseColor("#000066"));
			canvas.drawLine(0, offsetY + height, getWidth(), offsetY + height, linePaint1);

			textPaint.setColor(Color.parseColor("#000066"));
			canvas.drawText("" + position, getWidth() - 20, offsetY + height, textPaint);
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
				View v = mAdapter.getView(i, null, this);
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
	 * View转换为Bitmap
	 * @param view
	 */
	private Bitmap convertViewToBitmap(View view){
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		return view.getDrawingCache();
	}

	/**
	 * 根据旋转角计算竖直偏移，用于绘制
	 * @param degree
	 */
	private float calculateItemOffsetY(int degree){
		if(degree <= -90 || degree >= 90){
			return 0;
		}
		BigDecimal offsetA =
				new BigDecimal(mItemAngle)
				.divide(new BigDecimal(2.0), BigDecimal.ROUND_HALF_UP)
				.add(new BigDecimal(degree))
				.multiply(new BigDecimal(Math.PI))
				.divide(new BigDecimal(180), BigDecimal.ROUND_HALF_UP);
		BigDecimal y = new BigDecimal(mRadius)
				.multiply(new BigDecimal(1 - Math.sin(offsetA.doubleValue())));
		double offsetAngle = (degree + (mItemAngle >> 1)) * Math.PI / 180;
		double offsetY = mRadius * (1 - Math.sin(offsetAngle));
		return y.floatValue();
	}

	/**
	 * 根据旋转角度计算镜头需要拉远的距离
	 * @param degree
	 */
	private int calculateItemOffsetZ(int degree){
		if(degree <= -90 || degree >= 90){
			return 0;
		}
		double angle = degree * Math.PI / 180;
		double offsetZ = mRadius * (1 - Math.cos(angle));
		return (int) offsetZ;
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
	 * 根据弧长计算变化的弧度
	 * @param deltaY
	 */
	@Override
	protected int calculateScrollDegree(float deltaY, boolean addLastDegree){
		if(deltaY == 0){
			return 0;
		}
		boolean negative = deltaY < 0;
		double circumference = 2 * Math.PI * mRadius;
		int d = (int) (Math.abs(deltaY) * 360 / circumference) * (negative ? 1 : -1)
				+ (addLastDegree ? mLastScrollingDegree : 0);
		if(d < -(mCurrentItemIndex * mItemAngle)){
			d = -(mCurrentItemIndex * mItemAngle);
		}else if(d > (mItemCount - mCurrentItemIndex - 1) * mItemAngle){
			d = (mItemCount - mCurrentItemIndex - 1) * mItemAngle;
		}
		return d;
	}

	@Override
	protected int calculateScrollArcLength(float degree) {
		degree %= 360;
		return (int) (degree * Math.PI * mRadius / 180);
	}

	public void setSelectItem(int index){
		if(mAdapter == null || index < 0 || index >= mAdapter.getCount()){
			return;
		}
		mCurrentItemIndex = index;
	}
}
