package io.itimetraveler.widget.adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 基础控件，适配 WheelView 的Adapter
 *
 * Created by iTimeTraveler on 2017/12/9.
 */
public abstract class WheelAdapter extends BaseAdapter {
	private DataSetObservable mDataSetObservable = new DataSetObservable();

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.registerObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.unregisterObserver(observer);
	}

	/**
	 * Notifies the attached observers that the underlying data has been changed
	 * and any View reflecting the data set should refresh itself.
	 */
	@Override
	public void notifyDataSetChanged() {
		mDataSetObservable.notifyChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	@Override
	@Deprecated
	public boolean hasStableIds() {
		return false;
	}

	@Override
	@Deprecated
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	@Deprecated
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	@Deprecated
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	@Deprecated
	public CharSequence[] getAutofillOptions() {
		return new CharSequence[0];
	}
}
