package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itimetraveler.widget.wheelpicker.R;

/**
 * Created by xuewenlong on 2017/12/9.
 */
public class NumericWheelAdapter extends WheelAdapter {

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;

	/** The default gap */
	private static final int DEFAULT_VALUE_GAP = 1;

	// Values
	private int minValue;
	private int maxValue;
	private int gap = DEFAULT_VALUE_GAP;

	private LayoutInflater inflater;

	/**
	 * Default constructor
	 */
	public NumericWheelAdapter(Context context) {
		this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	}

	/**
	 * Constructor
	 * @param minValue the wheel min value
	 * @param maxValue the wheel max value
	 */
	public NumericWheelAdapter(Context context, int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return Math.abs(maxValue - minValue) + 1;
	}

	@Override
	public Object getItem(int index) {
		if (index >= 0 && index < getCount()) {
			return minValue + index;
		}
		return 0;
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
			convertView = inflater.inflate(R.layout.default_text_item_layout, null);
			viewHolder.textView = convertView.findViewById(R.id.default_text_item);
			convertView.setTag(viewHolder);
		}
		viewHolder.textView.setText("wheel-picker" + position);
		return convertView;
	}


	private static class ViewHolder{
		TextView textView;
	}
}
