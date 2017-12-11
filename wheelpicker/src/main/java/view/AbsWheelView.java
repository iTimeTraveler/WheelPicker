package view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;

import adapter.WheelAdapter;

/**
 * Created by iTimeTraveler on 2017/12/11.
 */
public abstract class AbsWheelView extends ViewGroup {
	private static final String TAG = "AbsWheelView";

	protected WheelAdapter mAdapter;
	protected DataSetObserver mDataSetObserver;

	//当前显示的第一个元素
	protected int mFirstPosition = 0;

	//当前选中的项
	protected int mCurrentSelectIndex = -1;

	//滑动的角度
	protected int mScrollDegree;

	//总数
	protected int mItemCount;
	protected int mOldItemCount;

	protected boolean mDataChanged = false;
	protected final boolean[] mIsScrap = new boolean[1];

	private GestureDetector mGestureDetector;
	private Scroller mScroller;

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
		mScroller = new Scroller(context);
		mGestureDetector = new GestureDetector(context, mOnGestureListener);
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
	public boolean onTouchEvent(MotionEvent event) {
		if(mAdapter == null) return false;

		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				mGestureDetector.onTouchEvent(event);
				break;
			case MotionEvent.ACTION_UP:
				mScrollDegree = 0;
				break;
		}
		return true;
	}

	/**
	 * 通过Adapter.getView重新加载一个View
	 */
	View obtainView(int position, boolean isScrap[]){
		isScrap[0] = false;
		final View scrapView = mRecycler.getScrapView(position);
		final View child = mAdapter.getView(position, scrapView, this);
		if (scrapView != null) {
			mRecycler.addScrapView(scrapView, position);
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

	protected void doScroll(float deltaY){}

	/**
	 * Subclasses must override this method to layout their children.
	 */
	protected void layoutChildren() {}


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
