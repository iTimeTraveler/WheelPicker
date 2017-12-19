package view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import adapter.WheelAdapter;

/**
 * Created by iTimeTraveler on 2017/12/19.
 */
public class TextWheelPicker extends WheelView {

	private TextAdapter mAdapter;
	private List<String> mStrList;

	/**
	 * Constructor
	 */
	public TextWheelPicker(Context context) {
		this(context, null);
	}

	/**
	 * Constructor
	 */
	public TextWheelPicker(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Constructor
	 */
	public TextWheelPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mAdapter = new TextAdapter(context);
		setAdapter(mAdapter);
	}

	public void setTextList(List<String> list){
		mStrList = list;
		mAdapter.setTextList(list);
		mAdapter.notifyDataSetChanged();
	}

	private static class TextAdapter extends WheelAdapter{
		private Context mContext;
		private List<String> mStrList;

		public TextAdapter(Context context){
			mContext = context;
		}

		public void setTextList(List<String> list){
			mStrList = list;
		}

		@Override
		public int getCount() {
			return (mStrList != null) ? mStrList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return (mStrList != null) ? mStrList.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;

			if(convertView != null){
				viewHolder = (ViewHolder) convertView.getTag();
			}else{
				viewHolder = new ViewHolder();
				RelativeLayout root = new RelativeLayout(mContext);
				root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				TextView tv = new TextView(mContext);
				tv.setPadding(20, 5, 20, 5);
				tv.setTextSize(20);

				//选中颜色
				int[] colors = new int[] {0xFF333333, 0xFFAAAAAA};
				int[][] states = {{android.R.attr.state_selected}, {}};
				tv.setTextColor(new ColorStateList(states, colors));
				root.addView(tv);

				viewHolder.textView = tv;
				convertView = root;
				convertView.setTag(viewHolder);
			}
			viewHolder.textView.setText(mStrList.get(position));
			return convertView;
		}
	}

	private static class ViewHolder{
		TextView textView;
	}
}
