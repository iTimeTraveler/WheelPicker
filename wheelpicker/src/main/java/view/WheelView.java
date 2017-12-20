package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Region;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

	private Paint mIndicatorPaint;
	private Paint mDustPaint;
	private LinearGradient mAboveGradient;
	private LinearGradient mBelowGradient;

	private Camera mCamera = new Camera();
	private Matrix mMatrix = new Matrix();

	//是否绘制辅助线
	private static final boolean DRAW_AUXILIARY_LINE = false;

	//指示器外侧元素缩放级别
	private static final float OUTER_ITEM_SCALE = 0.90F;

	//Camera远近
	private static final float CAMERA_LOCATION_Z = 10;

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
		initWheelView();
	}

	private void initWheelView(){
		mItemAngle = 180 / (SHOW_COUNT - 1);

		/* ViewGroup doesn't draw by default
		 *
		 * Typically, if you override {@link #onDraw(android.graphics.Canvas)}
		 *  you should clear this flag.
		 */
		setWillNotDraw(false);
		setSelectItem(0);
		initPaints();
	}

	private void initPaints() {
		mIndicatorPaint = new Paint();
		mIndicatorPaint.setColor(Color.parseColor("#C8C7CC"));
		mIndicatorPaint.setAntiAlias(true);

		float[] pos = new float[19];
		int[] x = getCircularGradientArray(0xAA, 0xFFFFFF, pos);
		mAboveGradient = new LinearGradient(getWidth()/2, getPaddingTop(), getWidth()/2, (getHeight() - mMaxItemHeight) / 2, x, pos, Shader.TileMode.CLAMP);
		mBelowGradient = new LinearGradient(getWidth()/2, getHeight() - getPaddingBottom(), getWidth()/2, (getHeight() + mMaxItemHeight) / 2, x, pos, Shader.TileMode.CLAMP);
		mDustPaint = new Paint();
	}

	/**
	 * 生成渐变色值组
	 * @param concentrate 起始点透明度
	 * @param background  遮罩颜色
	 * @param pos  锚点数组
	 * @return   colors数组
	 */
	private int[] getCircularGradientArray(int concentrate, int background, float[] pos){
		if(pos == null || pos.length < 10){
			pos = new float[10];
		}
		concentrate %= 256;
		int[] covers = new int[pos.length];
		for(int i = 0; i < pos.length; i++){
			pos[i] = ((float)(i)) / pos.length;

			//sin²x + cos²x = 1
			//cos x = Math.sqrt(1-sin²x)
			int dilute = (int) (concentrate * Math.sqrt(1 - Math.pow((0.1 * i), 2)));
			covers[i] = (dilute << 24) | background;
		}
		return covers;
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
			widthSize = mMaxItemWidth + getPaddingLeft() + getPaddingRight();
			heightSize = diameter + getPaddingTop() + getPaddingBottom();
		}else if(widthMode == MeasureSpec.AT_MOST){
			widthSize = mMaxItemWidth + getPaddingLeft() + getPaddingRight();
		}else if(heightMode == MeasureSpec.AT_MOST){
			heightSize = diameter + getPaddingTop() + getPaddingBottom();
		}

		mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
		if (mItemCount > 0 && (widthMode == MeasureSpec.UNSPECIFIED
				|| heightMode == MeasureSpec.UNSPECIFIED)) {
			final View child = obtainView(0, mIsScrap);

			// Lay out child directly against the parent measure spec so that
			// we can obtain exected minimum width and height.
			measureScrapChild(child, 0, widthMeasureSpec, heightSize);

			mRecycler.addScrapView(child, 0);
		}

		if (heightMode == MeasureSpec.AT_MOST) {
			// TODO: after first layout we should maybe start at the first visible position, not 0
			heightSize = measureHeightOfChildren(widthMeasureSpec, 0, NO_POSITION, heightSize, -1);
		}

		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void layoutChildren() {
		if (mAdapter == null) {
			return;
		}

		mRecycler.fillActiveViews(getChildCount(), mFirstPosition);

		// Clear out old views
		detachAllViewsFromParent();
		fillSpecific(mFirstPosition, getPaddingTop());
		initPaints();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int h = getHeight() - getPaddingTop() - getPaddingBottom();
		final int firstPos = mFirstPosition;
		if(mAdapter != null){
			int count = getChildCount();
			for(int i = 0; i < count; i++){
				View itemView = getChildAt(i);
				final int position = firstPos + i;

				//指示器内的view需要设置select状态，所以不能使用drawingCache得到位图
				boolean destroyDrawingCache = false;
				int degree = (mCurrentItemIndex - position) * mItemAngle + mScrollingDegree;
				if(degree < mItemAngle && degree > -mItemAngle){
					destroyDrawingCache = true;
					drawIndicatorItem(canvas, itemView, position, degree, true);
				}
				drawItem(canvas, itemView, position, degree, destroyDrawingCache);
			}
		}

		//绘制中间两条横线
		canvas.drawLine(0, ((h - mMaxItemHeight) >> 1) + getPaddingTop(), getWidth(),  ((h - mMaxItemHeight) >> 1) + getPaddingTop(), mIndicatorPaint);
		canvas.drawLine(0, ((h + mMaxItemHeight) >> 1) + getPaddingTop(), getWidth(),  ((h + mMaxItemHeight) >> 1) + getPaddingTop(), mIndicatorPaint);

		//蒙灰
		mDustPaint.setShader(mAboveGradient);
		canvas.drawRect(0, getPaddingTop(), getWidth(), ((h - mMaxItemHeight) >> 1) + getPaddingTop(), mDustPaint);
		mDustPaint.setShader(mBelowGradient);
		canvas.drawRect(0, ((h + mMaxItemHeight) >> 1) + getPaddingTop(), getWidth(), getHeight() - getPaddingBottom(), mDustPaint);

		//辅助线
		if(DRAW_AUXILIARY_LINE){
			Paint linePaint = new Paint();
			linePaint.setColor(Color.parseColor("#990000"));
			canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, linePaint);
		}
	}

	/**
	 * Don't need to draw child views here,
	 * they have been drawn on {@link #draw(Canvas)}
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		return;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * @return False to recycle the views used to measure this WheelView in
	 *         UNSPECIFIED/AT_MOST modes.
	 */
	private boolean recycleOnMeasure() {
		return false;
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
		final boolean recyle = recycleOnMeasure();
		final boolean[] isScrap = mIsScrap;

		for (i = startPosition; i <= endPosition; ++i) {
			child = obtainView(i, isScrap);

			measureScrapChild(child, i, widthMeasureSpec, maxHeight);

			if (i > 0) {
				// Count the divider for all but one child
				returnedHeight += dividerHeight;
			}

			// Recycle the view before we possibly return from the method
			if (recyle && recycleBin.shouldRecycleViewType(
					((LayoutParams) child.getLayoutParams()).viewType)) {
				recycleBin.addScrapView(child, -1);
			}

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
		Log.e(TAG, "fillSpecific() >>> mFirstPosition:" + mFirstPosition + ", mScrollingDegree:"+ mScrollingDegree + "， mCurrentItemIndex:" + mCurrentItemIndex);


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
		while (isDegreeVisible(getDeflectionDegree(pos)) && pos >= 0) {
			// is this the selected item?
			View child = makeAndAddView(pos, nextBottom, false, getPaddingLeft(), false);

			nextBottom = child.getTop();
			pos--;
		}
		mFirstPosition = pos + 1;
		Log.e(TAG, "fillUp() >>> mFirstPosition:" + mFirstPosition + ", mScrollingDegree:"+ mScrollingDegree + "， mCurrentItemIndex:" + mCurrentItemIndex);
	}

	/**
	 * Fills the list from pos down to the end of the list view.
	 *
	 * @param pos The first position to put in the list
	 * @param nextTop The location where the top of the item associated with pos
	 *        should be drawn
	 */
	private void fillDown(int pos, int nextTop) {
		while (isDegreeVisible(getDeflectionDegree(pos)) && pos < mItemCount) {
			// is this the selected item?
			View child = makeAndAddView(pos, nextTop, true, getPaddingLeft(), false);

			nextTop = child.getBottom();
			pos++;
		}
	}

	@Override
	protected void fillGap(boolean down) {
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
			Log.e(TAG, "fillGap("+down+") >>> mFirstPosition:" + mFirstPosition + ", mScrollingDegree:"+ mScrollingDegree + "， mCurrentItemIndex:" + mCurrentItemIndex);
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
	private int drawItem(Canvas canvas, View itemView, int position, int degree, boolean destoryDrawingCache){
		Bitmap bmp = convertViewToBitmap(itemView, destoryDrawingCache);
		int w = getWidth() - getPaddingLeft() - getPaddingRight();
		int h = getHeight() - getPaddingTop() - getPaddingBottom();
		int offsetZ = calculateItemOffsetZ(degree);
		float offsetY = calculateItemOffsetY(degree);
		int height = 0;

		if(bmp != null){
			height = calculateHeightAfterRotate(degree, bmp.getHeight());
			int offsetX = ((w - (int)(bmp.getWidth() * OUTER_ITEM_SCALE)) >> 1) + getPaddingLeft();

			mMatrix.reset();
			mCamera.save();
			//镜头距离，根据滚轴上元素的偏转角设置镜头远近
			mCamera.translate(mCamera.getLocationX(), mCamera.getLocationY(), CAMERA_LOCATION_Z + offsetZ);
			//绕X轴翻转
			mCamera.rotateX(degree);
			mCamera.getMatrix(mMatrix);
			mCamera.restore();
			mMatrix.preScale(OUTER_ITEM_SCALE, OUTER_ITEM_SCALE, - bmp.getWidth() / 2, - bmp.getHeight() / 2);
			//使用pre将旋转中心移动到和Camera位置相同。
			mMatrix.preTranslate(- bmp.getWidth() / 2, - bmp.getHeight() / 2);
			// 使用post将图片(View)移动到原来的位置
			mMatrix.postTranslate(bmp.getWidth() / 2, bmp.getHeight() / 2);

			canvas.save();
			canvas.clipRect(0, ((h - mMaxItemHeight) >> 1) + getPaddingTop(), getWidth(), ((h + mMaxItemHeight) >> 1) + getPaddingTop(), Region.Op.DIFFERENCE);
			canvas.translate(offsetX, offsetY);
			canvas.drawBitmap(bmp, mMatrix, null);
			//设置图片抗锯齿
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.restore();

			//辅助线
			if(DRAW_AUXILIARY_LINE){
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
		}

		return height;
	}

	/**
	 * 绘制选中放大区域
	 */
	private void drawIndicatorItem(Canvas canvas, View itemView, int position, int degree, boolean destoryDrawingCache){
		if(degree >= mItemAngle || degree <= -mItemAngle){
			return;
		}
		itemView.setSelected(true);
		Bitmap bmp = convertViewToBitmap(itemView, destoryDrawingCache);
		itemView.setSelected(false);
		int w = getWidth() - getPaddingLeft() - getPaddingRight();
		int h = getHeight() - getPaddingTop() - getPaddingBottom();
		int offsetZ = calculateItemOffsetZ(degree);
		float offsetY = calculateItemOffsetY(degree);

		if(bmp != null){
			int offsetX = ((w - bmp.getWidth()) >> 1) + getPaddingLeft();

			mMatrix.reset();
			mCamera.save();
			//镜头距离，根据滚轴上元素的偏转角设置镜头远近
			mCamera.translate(mCamera.getLocationX(), mCamera.getLocationY(), CAMERA_LOCATION_Z + offsetZ);
			//绕X轴翻转
			mCamera.rotateX(degree);
			mCamera.getMatrix(mMatrix);
			mCamera.restore();
			//使用pre将旋转中心移动到和Camera位置相同。
			mMatrix.preTranslate(- bmp.getWidth() / 2, - bmp.getHeight() / 2);
			// 使用post将图片(View)移动到原来的位置
			mMatrix.postTranslate(bmp.getWidth() / 2, bmp.getHeight() / 2);

			canvas.save();
			canvas.clipRect(0, ((h - mMaxItemHeight) >> 1) + getPaddingTop(), getWidth(), ((h + mMaxItemHeight) >> 1) + getPaddingTop());
			canvas.translate(offsetX, offsetY);
			canvas.drawBitmap(bmp, mMatrix, null);
			//设置图片抗锯齿
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.restore();
		}
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
	private Bitmap convertViewToBitmap(View view, boolean destoryCache){
		if(destoryCache){
			view.destroyDrawingCache();
		}
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
		double offsetAngle = (degree + (degree >= 0 ? 1 : -1) * (mItemAngle >> 1));
		double offsetRadians = offsetAngle * Math.PI / 180;
		double offsetY = mRadius * (1 - Math.sin(offsetRadians));
		//调整滚轴下方的元素
		if(degree < 0){
			offsetY -= mMaxItemHeight;
		}
		return (float) offsetY + getPaddingTop();
	}

	/**
	 * 根据旋转角度计算镜头需要拉远的距离
	 * @param degree
	 */
	private int calculateItemOffsetZ(int degree){
		if(degree <= -90 || degree >= 90){
			return 0;
		}
		double offsetRadians = degree * Math.PI / 180;
		double offsetZ = mRadius * (1 - Math.cos(offsetRadians));
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
		return (int) (degree * Math.PI * mRadius / 180);
	}

	@Override
	protected int getShowCount() {
		return SHOW_COUNT;
	}

	/**
	 * 设置选中项
	 * @param position
	 */
	public void setSelectItem(int position){
		if(mAdapter == null){
			return;
		}
		position = Math.max(Math.min(position, mAdapter.getCount() - 1), 0);
		int idealFirst = position - ((SHOW_COUNT - 2) >> 1);
		mCurrentItemIndex = position;
		mFirstPosition = Math.max(idealFirst, 0);

		requestLayout();
		invalidate();
		invokeOnItemScrollListener();
	}
}
