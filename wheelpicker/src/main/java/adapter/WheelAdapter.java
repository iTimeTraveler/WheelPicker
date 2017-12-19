package adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

/**
 * Created by iTimeTraveler on 2017/12/9.
 */
public abstract class WheelAdapter implements Adapter {
	private DataSetObservable dataSetObservable = new DataSetObservable();

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		dataSetObservable.registerObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		dataSetObservable.unregisterObserver(observer);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}
}
