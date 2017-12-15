package view;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.ArrayList;

import adapter.WheelAdapter;

/**
 * Created by iTimeTraveler on 2017/12/11.
 */
public abstract class AbsWheelView extends ViewGroup {

	private static final String TAG = "AbsWheelView";

	/** Scrolling duration */
	private static final int SCROLLING_DURATION = 400;

	/** Minimum delta for scrolling */
	private static final int MIN_DELTA_FOR_SCROLLING = 1;

	//滚动动画时间间隔
	private static final int SCROLLING_INTERVAL = 10;

	//可见的第一个元素
	protected int mFirstPosition = 0;

	//当前选中的项
	protected int mCurrentItemIndex = 0;

	// Scrolling
	private boolean isScrollingPerformed;

	//滑动的角度
	protected int mScrollingDegree;

	//item夹角
	protected int mItemAngle;

	protected float mLastDownY;
	protected float mLastMoveY;

	//总数
	protected int mItemCount;
	protected int mOldItemCount;

	//判定为拖动的最小移动像素数
	private int mTouchSlop;

	private Scroller mScroller;

	//手势识别
	private GestureDetector mGestureDetector;

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
		mScroller = new Scroller(context);
		mGestureDetector = new GestureDetector(context, mSimpleOnGestureListener);
		mGestureDetector.setIsLongpressEnabled(false);
	}

	/**
	 * Sets wheel adapter
	 * @param adapter the new wheel adapter
	 */
	public void setAdapter(WheelAdapter adapter){
		if(mAdapter != null && mDataSetObserver != null){
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		mAdapter = adapter;
		mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());

		requestLayout();
		invalidate();
	}

	/**
	 * Set the the specified scrolling interpolator
	 * @param interpolator the interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		mScroller.forceFinished(true);
		mScroller = new Scroller(getContext(), interpolator);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (mAdapter != null && mDataSetObserver == null) {
//			mDataSetObserver = new AdapterDataSetObserver();
//			mAdapter.registerDataSetObserver(mDataSetObserver);

			// Data may have changed while we were detached. Refresh.
			mDataChanged = true;
			mOldItemCount = mItemCount;
			mItemCount = mAdapter.getCount();
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
		if(mAdapter == null) return false;

		float rawY = event.getRawY();
//		switch (event.getAction()){
//			case MotionEvent.ACTION_DOWN:
//				mLastDownY = mLastMoveY = event.getRawY();
//				cancleScroll();
//				break;
//			case MotionEvent.ACTION_MOVE:
//				trackMotionScroll(rawY - mLastDownY, rawY - mLastMoveY);
//				mLastMoveY = rawY;
//				break;
//			case MotionEvent.ACTION_UP:
//				//TODO 当手指抬起时，根据当前的滚动值来判定应该自动滚动选中哪个子控件
//				// 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
////				rawY = event.getRawY();
////				trackMotionScroll(rawY - mLastDownY, rawY - mLastMoveY);
//
//				int degree = mScrollingDegree % mItemAngle;
//				if(Math.abs(degree) >= mItemAngle / 2){
//					mCurrentItemIndex += degree >= 0 ?
//							mScrollingDegree / mItemAngle + 1:
//							mScrollingDegree / mItemAngle - 1;
//
//					degree = degree >= 0 ?
//							degree - mItemAngle :
//							degree + mItemAngle;
//				}else{
//					mCurrentItemIndex += mScrollingDegree / mItemAngle;
//				}
//				mScrollingDegree = degree;
//
//				Message msg = new Message();
//				msg.what = MESSAGE_DO_SCROLL;
//				msg.arg1 = degree >= 0 ? Math.max(degree / 10, 1) : Math.min(degree / 10, -1);
//				mAnimHandler.removeMessages(MESSAGE_DO_SCROLL);
//				mAnimHandler.sendMessageDelayed(msg, SCROLLING_INTERVAL);
//				break;
//		}

		if (!mGestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
			rectify();	//手指抬起时校准
		}
		return true;
	}

	/**
	 * 手势监听 gesture listener
	 */
	private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

		@Override
		public boolean onDown(MotionEvent e) {
			Log.e("gesture", "onDown: " + e.toString());
			if (isScrollingPerformed) {
				mScroller.forceFinished(true);
				clearMessages();
				return true;
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.e("gesture", "onSingleTapUp: " + e.toString());
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent downE, MotionEvent moveE, float distanceX, float distanceY) {
			Log.e("gesture", "onScroll:============================start================================");
			Log.e("gesture", "onScroll: e1 >>> " + downE.toString());
			Log.e("gesture", "onScroll: e2 >>> " + moveE.toString());
			Log.e("gesture", "onScroll: moveE.getRawY() - downE.getRawY() >>> " + (moveE.getRawY() - downE.getRawY()));
			Log.e("gesture", "onScroll: distanceX >>> " + distanceX);
			Log.e("gesture", "onScroll: distanceY >>> " + distanceY);
			Log.e("gesture", "onScroll:============================end================================");

			startScrolling();
			trackMotionScroll(moveE.getRawY() - downE.getRawY(), -distanceY);
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.e("gesture", "onFling:----------------------------start----------------------------");
			Log.e("gesture", "onFling: e1 >>> " + e1.toString());
			Log.e("gesture", "onFling: e2 >>> " + e2.toString());
			Log.e("gesture", "onFling: velocityX >>> " + velocityX);
			Log.e("gesture", "onFling: velocityY >>> " + velocityY);
			Log.e("gesture", "onFling:----------------------------end----------------------------");
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
		mScrollingDegree = calculateScrollDegree(deltaY);
		final boolean goUp = incrementalDeltaY < 0;
		doScroll(goUp);
	}

	private void doScroll(boolean goUp){
		final int childCount = getChildCount();
		if (childCount == 0) {
			return;
		}

		final int firstPosition = mFirstPosition;
		int start = 0;	//需要回收的起始位置
		int count = 0;	//回收的View数量

		//往上滑动
		if(goUp){
			for (int i = 0; i < childCount; i++) {
				final View child = getChildAt(i);
				int position = firstPosition + i;
				int degree = getDeflectionDegree(position);
				if (isDegreeVisiable(degree)) {
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
				if (isDegreeVisiable(degree)) {
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
	 */
	private void rectify() {
		//根据余角设置最新选项
		int remainDegree = mScrollingDegree % mItemAngle;
//		if(Math.abs(remainDegree) >= mItemAngle / 2){
//			mCurrentItemIndex += remainDegree >= 0 ?
//					scrollingDegree / mItemAngle + 1:
//					scrollingDegree / mItemAngle - 1;
//
//			remainDegree += remainDegree >= 0 ? -mItemAngle : mItemAngle;
//		}else{
		mCurrentItemIndex += mScrollingDegree / mItemAngle;
		Log.e("rectify", mScrollingDegree+ "/" + mItemAngle + " = "+ mScrollingDegree / mItemAngle);
		Log.e("rectify", "mCurrentItemIndex=" + mCurrentItemIndex + ", remainDegree=" + remainDegree);
//		}
		mScrollingDegree = remainDegree;


		if (Math.abs(remainDegree) > MIN_DELTA_FOR_SCROLLING) {
			mScroller.startScroll(0, 0, 0, remainDegree, SCROLLING_DURATION);
			sendNextMessage(MESSAGE_DO_RECTIFY);
		} else {
			finishScrolling();
		}
	}

	/**
	 * 根据序号计算偏转角
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
	protected boolean isDegreeVisiable(int degree){
		return (degree >= -90 && degree <= 90);
	}

	/**
	 * 根据弧长计算变化的弧度
	 * @param deltaY
	 */
	abstract int calculateScrollDegree(float deltaY);

	// Messages
	private static final int MESSAGE_DO_SCROLL = 1;
	private static final int MESSAGE_DO_RECTIFY = 2;
	private static final int MESSAGE_STOP_SCROLL = 3;

	// animation handler
	private Handler mAnimHandler = new Handler(new Handler.Callback(){
		@Override
		public boolean handleMessage(Message msg) {
//			switch (msg.what){
//				case MESSAGE_DO_SCROLL:
//					if(msg.arg1 == 0){
//						return true;
//					}
//					mScrollingDegree -= msg.arg1;
//					if(Math.abs(mScrollingDegree) <= Math.abs(msg.arg1)){
//						mAnimHandler.removeMessages(MESSAGE_STOP_SCROLL);
//						mAnimHandler.sendEmptyMessageDelayed(MESSAGE_STOP_SCROLL, SCROLLING_INTERVAL);
//					}else{
//						doScroll(mScrollingDegree > 0);
//
//						Message message = new Message();
//						message.what = MESSAGE_DO_SCROLL;
//						message.arg1 = msg.arg1;
//						mAnimHandler.removeMessages(MESSAGE_DO_SCROLL);
//						mAnimHandler.sendMessageDelayed(message, SCROLLING_INTERVAL);
//					}
//					break;
//				case MESSAGE_STOP_SCROLL:
//					mScrollingDegree = 0;
//					doScroll(false);
//					mAnimHandler.removeMessages(MESSAGE_DO_SCROLL);
//					mAnimHandler.removeMessages(MESSAGE_STOP_SCROLL);
//					break;
//			}
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
		mAnimHandler.removeMessages(MESSAGE_DO_SCROLL);
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
		if (isScrollingPerformed) {
			isScrollingPerformed = false;
		}
		mScrollingDegree = 0;
		invalidate();
	}

	/**
	 * Subclasses must override this method to layout their children.
	 */
	protected void layoutChildren() {}

	/**
	 * Fills the gap left open by a touch-scroll. During a touch scroll, children that
	 * remain on screen are shifted and the other ones are discarded. The role of this
	 * method is to fill the gap thus created by performing a partial layout in the
	 * empty space.
	 *
	 * @param down true if the scroll is going down, false if it is going up
	 */
	abstract void fillGap(boolean down);


	/**
	 * View回收器
	 *
	 * The RecycleBin facilitates reuse of views across layouts. The RecycleBin has two levels of
	 * storage: ActiveViews and ScrapViews. ActiveViews are those views which were onscreen at the
	 * start of a layout. By construction, they are displaying current information. At the end of
	 * layout, all views in ActiveViews are demoted to ScrapViews. ScrapViews are old views that
	 * could potentially be used by the adapter to avoid allocating views unnecessarily.
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
		 * Unsorted views that can be used by the adapter as a convert view.
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

			mCurrentScrap.add(scrap);
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
			final ArrayList<View> scrap = mCurrentScrap;
			final int scrapCount = scrap.size();
			for (int i = 0; i < scrapCount; i++) {
				scrap.get(i).forceLayout();
			}
		}

		public boolean shouldRecycleViewType(int viewType) {
			return viewType >= 0;
		}
	}


	public static class LayoutParams extends ViewGroup.LayoutParams{
		int scrappedFromPosition;

		public LayoutParams(Context c, AttributeSet attrs) { super(c, attrs); }
		public LayoutParams(int width, int height) { super(width, height); }
		public LayoutParams(ViewGroup.LayoutParams source) { super(source); }
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
