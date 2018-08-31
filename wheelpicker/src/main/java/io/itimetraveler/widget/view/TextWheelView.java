package io.itimetraveler.widget.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import io.itimetraveler.widget.adapter.WheelAdapter;

/**
 * Created by iTimeTraveler on 2017/12/19.
 */
public class TextWheelView extends WheelView {

	private TextAdapter mAdapter;
	private List<String> mStrList;

	public TextWheelView(Context context) {
		this(context, null);
	}

	public TextWheelView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mAdapter = new TextAdapter(context);
		setAdapter(mAdapter);
	}

	public void setTextList(List<String> list){
		if (list == null || list.size() <= 0) return;
		mStrList = list;
		mAdapter.setTextList(list);
		mAdapter.notifyDataSetChanged();
		setSelectItem(0);
	}

	public void setTheme(Theme theme){
		switch (theme){
			case BLACK:
				setBackgroundColor(0xFF0D0D0D);
				setDefaultColor(0xFF626262);
				setSelectColor(0xFFCFCFCF);
				setDividerColor(0xFF3C3C3C);
				break;
			case WHITE:
			default:
				setBackgroundColor(0xFFFFFFFF);
				setDefaultColor(0xFFA4A4A4);
				setSelectColor(0xFF333333);
				setDividerColor(0xFFCDCDCD);
				break;
		}
	}

	/**
	 * 字体大小
	 * @param textSize
	 */
	public void setTextSize(int textSize){
		mAdapter.setTextSize(textSize);
	}

	public void setAutoSizeTextType(AutoFitTextView.AutoSizeTextType type) {
		mAdapter.setAutoSizeTextType(type);
	}

	/**
	 * 未选中项字体颜色
	 * @param color
	 */
	public void setDefaultColor(int color){
		mAdapter.setDefaultColor(color);
	}

	/**
	 * 选中项字体颜色
	 * @param selectColor
	 */
	public void setSelectColor(int selectColor) {
		mAdapter.setSelectColor(selectColor);
	}

	//主题色
	public enum Theme {
		WHITE, BLACK
	}

	private static class TextAdapter extends WheelAdapter{
		private Context context;
		private List<String> mStrList;
		private AutoFitTextView.AutoSizeTextType autoSizeTextType = AutoFitTextView.AutoSizeTextType.NONE;

		//默认配置
		private int mTextSize = 20;
		private int mDefaultColor = 0xFFAAAAAA;
		private int mSelectColor = 0xFF333333;

		TextAdapter(Context context){
			this.context = context;
		}

		void setTextList(List<String> list){
			mStrList = list;
		}

		void setTextSize(int textSize) {
			this.mTextSize = textSize;
		}

		void setDefaultColor(int defaultColor) {
			this.mDefaultColor = defaultColor;
		}

		void setSelectColor(int selectColor) {
			this.mSelectColor = selectColor;
		}

		void setAutoSizeTextType(AutoFitTextView.AutoSizeTextType type) {
			autoSizeTextType = type;
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
				AutoFitTextView tv = new AutoFitTextView(context);
				tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				tv.setPadding(20, 3, 20, 3);
				tv.setTextSize(mTextSize);
				tv.setAutoSizeTextType(autoSizeTextType);
				if (parent.getMeasuredWidth() > 0) {
					tv.setMaxWidth(parent.getMeasuredWidth());
				}

				//选中颜色
				int[] colors = new int[] {mSelectColor, mDefaultColor};
				int[][] states = {{android.R.attr.state_selected}, {}};
				tv.setTextColor(new ColorStateList(states, colors));

				viewHolder.textView = tv;
				convertView = tv;
				convertView.setTag(viewHolder);
			}
			viewHolder.textView.setText(mStrList.get(position), parent.getMeasuredWidth());
			return convertView;
		}
	}

	private static class ViewHolder{
		AutoFitTextView textView;
	}

	/**
	 * 自定义TextView，文本内容自动调整字体大小以适应TextView的大小
	 */
	static class AutoFitTextView extends android.support.v7.widget.AppCompatTextView {

		private float mInitTextSize;
		private AutoSizeTextType autoSizeTextType;

		public AutoFitTextView(Context context) {
			this(context, null);
		}

		public AutoFitTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.setMaxLines(1);
			this.setGravity(Gravity.CENTER);
		}

		void setAutoSizeTextType(AutoSizeTextType type) {
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
}
