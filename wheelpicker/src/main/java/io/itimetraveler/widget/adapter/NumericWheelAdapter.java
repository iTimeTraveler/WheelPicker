package io.itimetraveler.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itimetraveler.widget.wheelpicker.R;

import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2017/12/9.
 */
public class NumericWheelAdapter extends PickerAdapter {

	/** The default min value */
	private static final int DEFAULT_MIN_VALUE = 0;

	/** The default max value */
	private static final int DEFAULT_MAX_VALUE = 9;

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
		this(context, minValue, maxValue, DEFAULT_VALUE_GAP);
	}

	/**
	 * Constructor
	 * @param minValue the wheel min value
	 * @param maxValue the wheel max value
	 * @param gap gap
	 */
	public NumericWheelAdapter(Context context, int minValue, int maxValue, int gap) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.gap = gap;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View onCreateView(ViewGroup parent, int row, int component) {
		ViewHolder viewHolder = new ViewHolder();
		View convertView = inflater.inflate(R.layout.default_text_item_layout, null);
		viewHolder.textView = convertView.findViewById(R.id.default_text_item);
		viewHolder.textView.setText(String.valueOf(minValue + gap * row));
		convertView.setTag(viewHolder);
		return convertView;
	}

	@Override
	public void onBindView(ViewGroup parent, View convertView, int row, int component) {
		Object object = convertView.getTag();
		if (object instanceof ViewHolder) {
			((ViewHolder) object).textView.setText(String.valueOf(minValue + gap * row));
		}
	}

	@Override
	public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
		return 2;
	}

	@Override
	public int numberOfRowsInComponent(int component) {
		return Math.max((int)((maxValue - minValue) / gap), 1);
	}

	private static class ViewHolder{
		TextView textView;
	}
}
