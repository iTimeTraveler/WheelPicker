package io.itimetraveler.widget.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Scroller;

import java.util.ArrayList;

import io.itimetraveler.widget.adapter.WheelAdapter;
import io.itimetraveler.widget.utils.Logger;

/**
 * Created by iTimeTraveler on 2017/12/11.
 */
public abstract class AbsWheelView extends AdapterView<WheelAdapter> {

	private static final String TAG = AbsWheelView.class.getSimpleName();

	/** Scrolling duration */
	private static final int RECTIFY_ANIM_DURATION = 1000;

	/** Minimum delta for scrolling */
	private static final int MIN_DELTA_FOR_SCROLLING = 1;

	//可见的第一个元素, 用于回收机制
	protected int mFirstPosition = 0;

	//当前选中的项
	protected int mCurrentItemIndex = 0;

	// Scrolling
	private boolean isScrollingPerformed;

	//滑动的角度
	protected int mScrollingDegree;
	protected int mLastScrollingDegree;

	//item夹角
	protected int mItemAngle;

	//item高度
	protected int mMaxItemHeight;
	protected int mMaxItemWidth;

	protected float mLastMoveY;
	protected float mLastScrollY;
	protected float mLastFlingY;

	//总数
	protected int mItemCount;
	protected int mOldItemCount;

	//判定为拖动的最小移动像素数
	private int mTouchSlop;

	private Scroller mScroller;

	//手势识别
	private GestureDetector mGestureDetector;

	/**
	 * Optional callback to notify client when select position has changed
	 */
	private AbsWheelView.OnItemSelectedListener mOnItemSelectedListener;

	/**
	 * The adapter containing the data to be displayed by this view
	 */
	protected WheelAdapter mAdapter;

	/**
	 * Should be used by subclasses to listen to changes in the dataset
	 */
	protected DataSetObserver mDataSetObserver;

	/**
	 * True if the data has changed since the last layout
	 */
	protected boolean mDataChanged = false;

	protected final boolean[] mIsScrap = new boolean[1];

	/**
	 * Subclasses must retain their measure spec from onMeasure() into this member
	 */
	int mWidthMeasureSpec = 0;

	/**
	 * The data set used to store unused views that should be reused during the next layout
	 * to avoid creating new ones
	 */
	final RecycleBin mRecycler = new RecycleBin();

	/**
	 * Interface definition for a callback to be invoked when the wheel view's item
	 * has been selected.
	 */
	public interface OnItemSelectedListener {

		/**
		 * Callback method to be invoked while the wheel view's item is being selected.
		 * @param parentView
		 * @param position
		 */
		public void onItemSelected(AbsWheelView parentView, int position);
	}

	public AbsWheelView(Context context) {
		this(context, null);
	}

	public AbsWheelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AbsWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initData(context);
	}

	private void initData(Context context){
		ViewConfiguration configuration = ViewConfiguration.get(context);
		// 获取TouchSlop值
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
		mScroller = new Scroller(context, new DecelerateInterpolator());
		mGestureDetector = new GestureDetector(context, mSimpleOnGestureListener);
		mGestureDetector.setIsLongpressEnabled(false);
	}

	/**
	 * Sets wheel io.itimetraveler.widget.adapter
	 * @param adapter the new wheel io.itimetraveler.widget.adapter
	 */
	public void setAdapter(WheelAdapter adapter){
		if(mAdapter != null && mDataSetObserver != null){
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		mRecycler.clear();

		mAdapter = adapter;
		mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());

		requestLayout();
		invalidate();
	}

	public WheelAdapter getAdapter() {
		return mAdapter;
	}


	/**
	 * Set the listener that will receive notifications when the wheel view
	 * finishes scrolling and select an option automatically.
	 * @param l the item selected listener
	 */
	public void setOnItemSelectedListener(AbsWheelView.OnItemSelectedListener l) {
		mOnItemSelectedListener = l;
	}

	/**
	 * Set the the specified scrolling interpolator
	 * @param interpolator the interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		mScroller.forceFinished(true);
		mScroller = new Scroller(getContext(), interpolator);
	}

	/**
	 * Notify our item selected listener (if there is one) of a change after finishing scrolling.
	 */
	protected void invokeOnItemScrollListener() {
		if (mOnItemSelectedListener != null) {
			mOnItemSelectedListener.onItemSelected(this, mCurrentItemIndex);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (mAdapter != null && mDataSetObserver == null) {
			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);

			// Data may have changed while we were detached. Refresh.
			mDataChanged = true;
			mOldItemCount = mItemCount;
			mItemCount = mAdapter.getCount();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		clearMessages();
		super.onDetachedFromWindow();

		// Detach any view left in the scrap heap
		mRecycler.clear();

		if (mAdapter != null && mDataSetObserver != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
			mDataSetObserver = null;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				getChildAt(i).forceLayout();
			}
			mRecycler.markChildrenDirty();
		}
		layoutChildren();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (getChildCount() > 0) {
			mDataChanged = true;
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public View getSelectedView() {
		if (mItemCount > 0 && mCurrentItemIndex >= 0) {
			return getChildAt(mCurrentItemIndex - mFirstPosition);
		} else {
			return null;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()){
			case MotionEvent.ACTION_MOVE:
				float delta = ev.getRawX() - mLastMoveY;
				mLastMoveY = ev.getRawY();
				//当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
				if(delta > mTouchSlop){
					return true;
				}
				break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mAdapter == null) {
			return false;
		}

		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if (null != getParent()) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				break;
		}

		if (!mGestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
			rectify(false);	//手指抬起时校准
		}
		return true;
	}

	/**
	 * 手势监听 gesture listener
	 */
	private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

		@Override
		public boolean onDown(MotionEvent e) {
			Logger.e("gesture", "onDown: " + e.toString());
			if (isScrollingPerformed) {
				mLastScrollingDegree = mScrollingDegree;
				mScroller.forceFinished(true);
				clearMessages();
				Logger.e("ondown=====", "mScrollingDegree:"+ mScrollingDegree + ", mLastScrollingDegree:"+ mLastScrollingDegree + ", mCurrentItemIndex:"+ mCurrentItemIndex);
				return true;
			}
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Logger.e("gesture", "onSingleTapUp: " + e.toString());
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent downE, MotionEvent moveE, float distanceX, float distanceY) {
			Logger.e("gesture", "onScroll:============================start================================");
			Logger.e("gesture", "onScroll: e1 >>> " + downE.toString());
			Logger.e("gesture", "onScroll: e2 >>> " + moveE.toString());
			Logger.e("gesture", "onScroll: moveE.getRawY() - downE.getRawY() >>> " + (moveE.getRawY() - downE.getRawY()));
			Logger.e("gesture", "onScroll: distanceX >>> " + distanceX);
			Logger.e("gesture", "onScroll: distanceY >>> " + distanceY);
			Logger.e("gesture", "onScroll:============================end================================");

			startScrolling();
			trackMotionScroll(moveE.getRawY() - downE.getRawY(), -distanceY);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Logger.e("gesture", "onFling:----------------------------start----------------------------");
			Logger.e("gesture", "onFling: e1 >>> " + e1.toString());
			Logger.e("gesture", "onFling: e2 >>> " + e2.toString());
			Logger.e("gesture", "onFling: velocityX >>> " + velocityX);
			Logger.e("gesture", "onFling: velocityY >>> " + velocityY);
			Logger.e("gesture", "onFling:----------------------------end----------------------------");

			int startY = calculateScrollArcLength(mScrollingDegree);
			int minY = -(calculateScrollArcLength(mCurrentItemIndex * mItemAngle));
			int maxY = calculateScrollArcLength((mItemCount - mCurrentItemIndex - 1) * mItemAngle);
			Logger.e("MESSAGE_DO_FLING=====", "mScrollingDegree:"+ mScrollingDegree + ", startY:"+ startY+ " ,velocityY:" + -velocityY + ", minY:"+ minY + ", maxY:"+ maxY);
			mLastFlingY = startY;
			mScroller.fling(0, startY, 0, (int) -velocityY, 0, 0, minY, maxY);
			sendNextMessage(MESSAGE_DO_FLING);
			return true;
		}
	};

	/**
	 * 通过Adapter.getView重新加载一个View
	 */
	View obtainView(int position, boolean isScrap[]){
		isScrap[0] = false;
		final View scrapView = mRecycler.getScrapView(position);
		final View child = mAdapter.getView(position, scrapView, this);

		if (scrapView != null) {
			if (child != scrapView) {
				// Failed to re-bind the data, return scrap to the heap.
				mRecycler.addScrapView(scrapView, position);
			}
		}
		setItemViewLayoutParams(child, position);
		return child;
	}

	private void setItemViewLayoutParams(View child, int position) {
		final ViewGroup.LayoutParams vlp = child.getLayoutParams();
		LayoutParams lp;
		if (vlp == null) {
			lp = (LayoutParams) generateDefaultLayoutParams();
		} else if (!checkLayoutParams(vlp)) {
			lp = (LayoutParams) generateLayoutParams(vlp);
		} else {
			lp = (LayoutParams) vlp;
		}

		lp.viewType = mAdapter.getItemViewType(position);
		if (lp != vlp) {
			child.setLayoutParams(lp);
		}
	}

	/**
	 * Track a motion scroll 滚动事件
	 *
	 * @param deltaY Down事件以来移动的总距离
	 * @param incrementalDeltaY Move事件的移动距离
	 *
	 * @param deltaY Amount to offset mMotionView. This is the accumulated delta since the motion
	 *        began. Positive numbers mean the user's finger is moving down the screen.
	 * @param incrementalDeltaY Change in deltaY from the previous event.
	 */
	protected void trackMotionScroll(float deltaY, float incrementalDeltaY){
		final int childCount = getChildCount();
		if (childCount == 0) {
			return;
		}
		mScrollingDegree = calculateScrollDegree(deltaY, true);
		final boolean goUp = incrementalDeltaY < 0;
		doScroll(goUp, "trackMotionScroll");
	}

	/**
	 * 滚动时使用回收策略复用View
	 * @param goUp 是否是向上滚动
	 */
	private void doScroll(boolean goUp, String src){
		final int childCount = getChildCount();
		if (childCount == 0) {
			return;
		}
		Logger.e(TAG, "doScroll() >>> src:" + src + ", goUp:" + goUp + " , mFirstPosition:" + mFirstPosition+ ", mScrollingDegree:"+ mScrollingDegree + "， mCurrentItemIndex:" + mCurrentItemIndex);

		final int firstPosition = mFirstPosition;
		int start = 0;	//需要回收的起始位置
		int count = 0;	//回收的View数量

		//往上滑动
		if(goUp){
			for (int i = 0; i < childCount; i++) {
				final View child = getChildAt(i);
				int position = firstPosition + i;
				int degree = getDeflectionDegree(position);
				if (isDegreeVisible(degree)) {
					break;
				} else {
					count++;
					mRecycler.addScrapView(child, position);
				}
			}
		}else{	//往下滑动
			for (int i = childCount - 1; i >= 0; i--) {
				final View child = getChildAt(i);
				int position = firstPosition + i;
				int degree = getDeflectionDegree(position);
				if (isDegreeVisible(degree)) {
					break;
				} else {
					start = i;
					count++;
					mRecycler.addScrapView(child, position);
				}
			}
		}

		if (count > 0) {
			detachViewsFromParent(start, count);
		}

		if (goUp) {
			mFirstPosition += count;
		}

		mRecycler.fullyDetachScrapViews();

		//填补空白区域
		fillGap(goUp);
		postInvalidate();
	}

	/**
	 * 校准选中 rectify wheel
	 * @param inertia 是否惯性滚动
	 */
	private void rectify(boolean inertia) {
		//根据余角计算最新选项
		int scrollingDegree = mScrollingDegree;
		int remainDegree = scrollingDegree % mItemAngle;
		int idealCount = scrollingDegree / mItemAngle;
		int validCount = 0;

		//滚动角度超过mItemAngle就顺势定位到下一个
		if(inertia || Math.abs(remainDegree) >= mItemAngle / 2){
			idealCount += (remainDegree == 0) ? 0 : ((remainDegree > 0) ? 1 : -1);
			remainDegree += (remainDegree == 0) ? 0 : ((remainDegree > 0) ? -mItemAngle : mItemAngle);
		}

		//如果算出来的定位偏移数量越界
		if(idealCount > (mItemCount - mCurrentItemIndex - 1)){
			validCount = mItemCount - mCurrentItemIndex - 1;
			mScrollingDegree = 0;
		}else if(idealCount < -mCurrentItemIndex){
			validCount = -mCurrentItemIndex;
			mScrollingDegree = 0;
		}else{
			validCount = idealCount;
			mScrollingDegree = remainDegree;
		}

		Logger.e("rectify before", "mCurrentItemIndex=" + mCurrentItemIndex + ", mScrollingDegree=" + mScrollingDegree);

		mCurrentItemIndex += validCount;

		Logger.e("rectify after", "mCurrentItemIndex=" + mCurrentItemIndex + ", mScrollingDegree=" + mScrollingDegree);


//		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
//		for (StackTraceElement elements : map.get(Thread.currentThread())){
//			Logger.e("rectify -- stacks", ""+ elements.toString());
//		}
		Logger.e("rectify", scrollingDegree+ "/" + mItemAngle + " = "+ scrollingDegree / mItemAngle);
		Logger.e("rectify", "mCurrentItemIndex=" + mCurrentItemIndex + ", mScrollingDegree=" + mScrollingDegree);

		//使用动画滚动到选中位置
		if (Math.abs(mScrollingDegree) > MIN_DELTA_FOR_SCROLLING) {
			mLastScrollY = 0;
			int duration = (int) Math.min(RECTIFY_ANIM_DURATION, Math.max(RECTIFY_ANIM_DURATION*0.3, Math.abs(RECTIFY_ANIM_DURATION * mScrollingDegree * 1.0F / mItemAngle)));
			mScroller.startScroll(0, 0, 0, mScrollingDegree, duration);
			Logger.e("rectify", "duration =" + duration + ", mScrollingDegree:" + mScrollingDegree);
			sendNextMessage(MESSAGE_DO_RECTIFY);
		} else {
			finishScrolling();
		}
	}

	/**
	 * 根据序号计算偏转角, 度数上方为正，下方为负
	 *
	 *          · · · +
	 *      ·             +
	 *    ·                 +
	 *   ·                   +
	 *  ·                     +
	 *  ·          0          0
	 *  ·                     -
	 *   ·                   -
	 *    ·                 -
	 *      ·             -
	 *          · · · -
	 *
	 * @param position
	 */
	protected int getDeflectionDegree(int position){
		if(position < 0 || position > mItemCount){
			return Integer.MIN_VALUE;
		}
		int offsetDegree = (mCurrentItemIndex - position) * mItemAngle + mScrollingDegree;
		return offsetDegree;
	}

	/**
	 * 偏转之后是否可见，用于回收策略
	 * @param degree
	 */
	protected boolean isDegreeVisible(int degree){
		return (degree > -90 && degree < 90);
	}

	/**
	 * 根据弧长计算变化的弧度
	 * @param deltaY
	 */
	protected abstract int calculateScrollDegree(float deltaY, boolean addLastDegree);

	protected abstract int calculateScrollArcLength(float degree);

	// Messages
	private static final int MESSAGE_DO_FLING = 1;
	private static final int MESSAGE_DO_RECTIFY = 2;
	private static final int MESSAGE_STOP_SCROLL = 3;

	// animation handler
	private Handler mAnimHandler = new Handler(new Handler.Callback(){
		@Override
		public boolean handleMessage(Message msg) {
			mScroller.computeScrollOffset();
			int currY = mScroller.getCurrY();

			switch (msg.what){
				case MESSAGE_DO_FLING:

					int velocityDegree = calculateScrollDegree(mLastFlingY - currY, false);
					Logger.e("MESSAGE_DO_FLING before", "mScrollingDegree:" + mScrollingDegree +", currY:"+ currY + ", mScroller.getFinalY():" +mScroller.getFinalY() + ", mLastFlingY:"+ mLastFlingY  + ", mScroller.isFinished():" + mScroller.isFinished());
					Logger.e("MESSAGE_DO_FLING before", "velocityDegree:" + velocityDegree  + ", mCurrentItemIndex:" + mCurrentItemIndex);

					mScrollingDegree += velocityDegree;
					mLastFlingY = currY;
					doScroll(velocityDegree > 0, "MESSAGE_DO_FLING");

					Logger.e("MESSAGE_DO_FLING **", "mScrollingDegree:" + mScrollingDegree +", currY:"+ currY + ", mScroller.getFinalY():" +mScroller.getFinalY() + ", mLastFlingY:"+ mLastFlingY  + ", mScroller.isFinished():" + mScroller.isFinished());

					// scrolling is not finished when it comes to final Y
					// so, finish it manually
					if (Math.abs(velocityDegree) <= MIN_DELTA_FOR_SCROLLING) {
						mScroller.forceFinished(true);
					}
					if (!mScroller.isFinished()) {
						sendNextMessage(msg.what);
					} else {
						rectify(true);
					}
					break;
				case MESSAGE_DO_RECTIFY:
					Logger.e("MESSAGE_DO_RECTIFY befo", "mScrollingDegree:" + mScrollingDegree +", currY:"+ currY + ", mScroller.getFinalY():" +mScroller.getFinalY() + ", mLastScrollY:"+ mLastScrollY  + ", mScroller.isFinished():" + mScroller.isFinished());

					mScrollingDegree -= currY - mLastScrollY;
					mLastScrollY = currY;
					doScroll(mScrollingDegree > 0, "MESSAGE_DO_RECTIFY");

					Logger.e("MESSAGE_DO_RECTIFY", "mScrollingDegree:" + mScrollingDegree +", currY:"+ currY + ", mScroller.getFinalY():" +mScroller.getFinalY() + ", mLastScrollY:"+ mLastScrollY  + ", mScroller.isFinished():" + mScroller.isFinished());

					// scrolling is not finished when it comes to final Y
					// so, finish it manually
					if (Math.abs(currY - mScroller.getFinalY()) <= MIN_DELTA_FOR_SCROLLING) {
						mScroller.forceFinished(true);
					}
					if (!mScroller.isFinished()) {
						sendNextMessage(msg.what);
					} else {
						finishScrolling();
					}
					break;
			}
			return true;
		}
	});

	/**
	 * Set next message to queue. Clears queue before.
	 * @param message the message to set
	 */
	private void sendNextMessage(int message) {
		clearMessages();
		mAnimHandler.sendEmptyMessage(message);
	}

	/**
	 * Clears messages from queue
	 */
	private void clearMessages() {
		mAnimHandler.removeMessages(MESSAGE_DO_FLING);
		mAnimHandler.removeMessages(MESSAGE_DO_RECTIFY);
		mAnimHandler.removeMessages(MESSAGE_STOP_SCROLL);
	}

	/**
	 * Starts scrolling
	 */
	private void startScrolling() {
		if (!isScrollingPerformed) {
			isScrollingPerformed = true;
		}
	}

	/**
	 * Finishes scrolling
	 */
	void finishScrolling() {
		Logger.e(TAG, "finishScrolling() >>> " + mScrollingDegree + ", mLastScrollingDegree:" + mLastScrollingDegree);
		if (isScrollingPerformed) {
			isScrollingPerformed = false;
		}
		mScrollingDegree = 0;
		mLastScrollingDegree = 0;

		postInvalidate();
		invokeOnItemScrollListener();
	}

	/**
	 * Subclasses must override this method to layout their children.
	 */
	protected void layoutChildren() {}
	protected int getShowCount() {
		return 0;
	}

	/**
	 * Fills the gap left open by a touch-scroll. During a touch scroll, children that
	 * remain on screen are shifted and the other ones are discarded. The role of this
	 * method is to fill the gap thus created by performing a partial layout in the
	 * empty space.
	 *
	 * @param down true if the scroll is going down, false if it is going up
	 */
	protected abstract void fillGap(boolean down);


	/**
	 * View回收器
	 *
	 * The RecycleBin facilitates reuse of views across layouts. The RecycleBin has two levels of
	 * storage: ActiveViews and ScrapViews. ActiveViews are those views which were onscreen at the
	 * start of a layout. By construction, they are displaying current information. At the end of
	 * layout, all views in ActiveViews are demoted to ScrapViews. ScrapViews are old views that
	 * could potentially be used by the io.itimetraveler.widget.adapter to avoid allocating views unnecessarily.
	 */
	class RecycleBin{
		/**
		 * The position of the first view stored in mActiveViews.
		 */
		private int mFirstActivePosition;

		/**
		 * Views that were on screen at the start of layout. This array is populated at the start of
		 * layout, and at the end of layout all view in mActiveViews are moved to mScrapViews.
		 * Views in mActiveViews represent a contiguous range of Views, with position of the first
		 * view store in mFirstActivePosition.
		 */
		private View[] mActiveViews = new View[0];

		/**
		 * Unsorted views that can be used by the io.itimetraveler.widget.adapter as a convert view.
		 */
		private ArrayList<View>[] mScrapViews;

		private ArrayList<View> mCurrentScrap;

		private int mViewTypeCount;

		public void setViewTypeCount(int viewTypeCount) {
			if (viewTypeCount < 1) {
				throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
			}
			//noinspection unchecked
			ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
			for (int i = 0; i < viewTypeCount; i++) {
				scrapViews[i] = new ArrayList<View>();
			}
			mViewTypeCount = viewTypeCount;
			mCurrentScrap = scrapViews[0];
			mScrapViews = scrapViews;
		}


		/**
		 * Fill ActiveViews with all of the children of the AbsListView.
		 *
		 * @param childCount The minimum number of views mActiveViews should hold
		 * @param firstActivePosition The position of the first view that will be stored in
		 *        mActiveViews
		 */
		void fillActiveViews(int childCount, int firstActivePosition) {
			if (mActiveViews.length < childCount) {
				mActiveViews = new View[childCount];
			}
			mFirstActivePosition = firstActivePosition;

			//noinspection MismatchedReadAndWriteOfArray
			final View[] activeViews = mActiveViews;
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				AbsWheelView.LayoutParams lp = (AbsWheelView.LayoutParams) child.getLayoutParams();

				// Don't put header or footer views into the scrap heap
				// Note:  We do place AdapterView.ITEM_VIEW_TYPE_IGNORE in active views.
				//        However, we will NOT place them into scrap views.
				activeViews[i] = child;
				// Remember the position so that setupChild() doesn't reset state.
				lp.scrappedFromPosition = firstActivePosition + i;
			}
		}

		/**
		 * Get the view corresponding to the specified position. The view will be removed from
		 * mActiveViews if it is found.
		 *
		 * @param position The position to look up in mActiveViews
		 * @return The view if it is found, null otherwise
		 */
		View getActiveView(int position) {
			int index = position - mFirstActivePosition;
			final View[] activeViews = mActiveViews;
			if (index >=0 && index < activeViews.length) {
				final View match = activeViews[index];
				activeViews[index] = null;
				return match;
			}
			return null;
		}

		/**
		 * Put a view into the ScapViews list. These views are unordered.
		 *
		 * @param scrap
		 *            The view to add
		 */
		void addScrapView(View scrap, int position) {
			AbsWheelView.LayoutParams lp = (AbsWheelView.LayoutParams) scrap.getLayoutParams();
			if (lp == null) {
				return;
			}
			lp.scrappedFromPosition = position;

			// Remove but don't scrap header or footer views, or views that
			// should otherwise not be recycled.
			final int viewType = lp.viewType;
			if (mViewTypeCount == 1) {
				mCurrentScrap.add(scrap);
			} else {
				mScrapViews[viewType].add(scrap);
			}
		}

		/**
		 * @return A view from the ScrapViews collection. These are unordered.
		 */
		View getScrapView(int position) {
			final int whichScrap = mAdapter.getItemViewType(position);
			if (whichScrap < 0) {
				return null;
			}
			return retrieveFromScrap(mCurrentScrap, position);
		}

		private View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
			final int size = scrapViews.size();
			if (size > 0) {
				return scrapViews.remove(size - 1);
			} else {
				return null;
			}
		}

		/**
		 * At the end of a layout pass, all temp detached views should either be re-attached or
		 * completely detached. This method ensures that any remaining view in the scrap list is
		 * fully detached.
		 */
		void fullyDetachScrapViews() {
			final int viewTypeCount = mViewTypeCount;
			final ArrayList<View>[] scrapViews = mScrapViews;
			for (int i = 0; i < viewTypeCount; ++i) {
				final ArrayList<View> scrapPile = scrapViews[i];
				for (int j = scrapPile.size() - 1; j >= 0; j--) {
					final View view = scrapPile.get(j);
					removeDetachedView(view, false);
				}
			}
		}

		public void markChildrenDirty() {
			if (mViewTypeCount == 1 && mCurrentScrap != null) {
				final ArrayList<View> scrap = mCurrentScrap;
				final int scrapCount = scrap.size();
				for (int i = 0; i < scrapCount; i++) {
					scrap.get(i).forceLayout();
				}
			}
		}

		public boolean shouldRecycleViewType(int viewType) {
			return viewType >= 0;
		}

		/**
		 * Clears the scrap heap.
		 */
		void clear() {
			if (mViewTypeCount == 1) {
				final ArrayList<View> scrap = mCurrentScrap;
				clearScrap(scrap);
			} else {
				final int typeCount = mViewTypeCount;
				for (int i = 0; i < typeCount; i++) {
					final ArrayList<View> scrap = mScrapViews[i];
					clearScrap(scrap);
				}
			}
		}

		private void clearScrap(final ArrayList<View> scrap) {
			final int scrapCount = scrap.size();
			for (int j = 0; j < scrapCount; j++) {
				removeDetachedView(scrap.remove(scrapCount - 1 - j), false);
			}
		}
	}

	class AdapterDataSetObserver extends DataSetObserver {

		private Parcelable mInstanceState = null;

		@Override
		public void onChanged() {
			mDataChanged = true;
			mOldItemCount = mItemCount;
			mItemCount = getAdapter().getCount();
			mCurrentItemIndex = 0;
			mFirstPosition = 0;

//			// Detect the case where a cursor that was previously invalidated has
//			// been repopulated with new data.
//			if (AdapterView.this.getAdapter().hasStableIds() && mInstanceState != null
//					&& mOldItemCount == 0 && mItemCount > 0) {
//				AdapterView.this.onRestoreInstanceState(mInstanceState);
//				mInstanceState = null;
//			} else {
//				rememberSyncState();
//			}
//			checkFocus();
			clearMessages();
			finishScrolling();
			mCurrentItemIndex = 0;
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			mDataChanged = true;

//			if (AdapterView.this.getAdapter().hasStableIds()) {
//				// Remember the current state for the case where our hosting activity is being
//				// stopped and later restarted
//				mInstanceState = AdapterView.this.onSaveInstanceState();
//			}

			// Data is invalid so we should reset our state
			mOldItemCount = mItemCount;
			mItemCount = 0;
//			mSelectedPosition = INVALID_POSITION;
//			mSelectedRowId = INVALID_ROW_ID;
//			mNextSelectedPosition = INVALID_POSITION;
//			mNextSelectedRowId = INVALID_ROW_ID;
//			mNeedSync = false;
//
//			checkFocus();
			clearMessages();
			requestLayout();
		}

		public void clearSavedState() {
			mInstanceState = null;
		}
	}


	public static class LayoutParams extends ViewGroup.LayoutParams{
		/**
		 * View type for this view, as returned by
		 * {@link android.widget.Adapter#getItemViewType(int) }
		 */
		int viewType;
		int scrappedFromPosition;

		public LayoutParams(Context c, AttributeSet attrs) { super(c, attrs); }
		public LayoutParams(int width, int height) { super(width, height); }
		public LayoutParams(ViewGroup.LayoutParams source) { super(source); }
		public LayoutParams(int w, int h, int viewType) {
			super(w, h);
			this.viewType = viewType;
		}
	}


	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new AbsWheelView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new AbsWheelView.LayoutParams(getContext(), attrs);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof AbsWheelView.LayoutParams;
	}
}
