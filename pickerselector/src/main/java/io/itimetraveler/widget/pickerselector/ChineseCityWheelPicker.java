package io.itimetraveler.widget.pickerselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.itimetraveler.widget.adapter.PickerAdapter;
import io.itimetraveler.widget.model.IPickerItemView;
import io.itimetraveler.widget.model.PickerNode;
import io.itimetraveler.widget.model.StringItemView;
import io.itimetraveler.widget.picker.PicketOptions;
import io.itimetraveler.widget.picker.WheelPicker;

/**
 * Created by iTimeTraveler on 2019/3/20.
 */
public class ChineseCityWheelPicker extends WheelPicker {
    
    private OnCitySelectListener mOnCitySelectListener;


    public ChineseCityWheelPicker(Context context) {
        this(context, null);
    }

    public ChineseCityWheelPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChineseCityWheelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        final List<PickerNode<IPickerItemView>> provinces = getProvincesData();
        if (provinces == null) {
            return;
        }

        PickerAdapter adapter = new PickerAdapter() {
            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return 3;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                int[] selectPos = ChineseCityWheelPicker.this.getSelectedPositions();
                switch (component) {
                    case 0:
                        return provinces.size();
                    case 1:
                        return provinces.get(selectPos[0]).getNextLevel().size();
                    case 2:
                        return provinces.get(selectPos[0]).getNextLevel().get(selectPos[1]).getNextLevel().size();
                }
                return 0;
            }

            @Override
            public View onCreateView(ViewGroup parent, int row, int component) {
                int[] selectPos = ChineseCityWheelPicker.this.getSelectedPositions();
                switch (component) {
                    case 0:
                        return provinces.get(row).getData().onCreateView(parent);
                    case 1:
                        return provinces.get(selectPos[0]).getNextLevel().get(row).getData().onCreateView(parent);
                    case 2:
                        return provinces.get(selectPos[0]).getNextLevel().get(selectPos[1]).getNextLevel().get(row).getData().onCreateView(parent);
                }
                return null;
            }

            @Override
            public void onBindView(ViewGroup parent, View convertView, int row, int component) {
                int[] selectPos = ChineseCityWheelPicker.this.getSelectedPositions();
                switch (component) {
                    case 0:
                        provinces.get(row).getData().onBindView(parent, convertView, row);
                        break;
                    case 1:
                        provinces.get(selectPos[0]).getNextLevel().get(row).getData().onBindView(parent, convertView, row);
                        break;
                    case 2:
                        provinces.get(selectPos[0]).getNextLevel().get(selectPos[1]).getNextLevel().get(row).getData().onBindView(parent, convertView, row);
                        break;
                }
            }
        };

        setOptions(new PicketOptions.Builder()
                .linkage(true)
                .build());
        setAdapter(adapter);
        setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {

                // 排除联动产生的非法选项
                if (position[0] >= provinces.size() || position[1] >= provinces.get(position[0]).getNextLevel().size()
                        || position[2] >= provinces.get(position[0]).getNextLevel().get(position[1]).getNextLevel().size()) {
                    return;
                }

                String province = ((StringItemView) provinces.get(position[0]).getData()).getData();
                String city = ((StringItemView) provinces.get(position[0]).getNextLevel()
                        .get(position[1]).getData()).getData();
                String area = ((StringItemView) provinces.get(position[0]).getNextLevel()
                        .get(position[1]).getNextLevel()
                        .get(position[2]).getData()).getData();
                if (mOnCitySelectListener != null) {
                    mOnCitySelectListener.OnCitySelected(ChineseCityWheelPicker.this, province, city, area);
                }
            }
        });
    }

    private List<PickerNode<IPickerItemView>> getProvincesData() {
        try {
            InputStream is = mContext.getAssets().open("provinces.json");
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            final String result = new String(buffer, "utf8");

            final List<PickerNode<IPickerItemView>> provinces = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.optJSONObject(i);
                String pname = o.optString("name");
                PickerNode<IPickerItemView> pnode = new PickerNode<IPickerItemView>(new StringItemView(pname));

                List<PickerNode<IPickerItemView>> city = new ArrayList<>();
                JSONArray cityArray = o.optJSONArray("city");
                for (int j = 0; cityArray != null && j < cityArray.length(); j++) {
                    JSONObject c = cityArray.optJSONObject(j);
                    String cname = c.optString("name");
                    PickerNode<IPickerItemView> cnode = new PickerNode<IPickerItemView>(new StringItemView(cname));

                    List<PickerNode<IPickerItemView>> area = new ArrayList<>();
                    JSONArray areaArray = c.optJSONArray("area");
                    for (int k = 0; areaArray != null && k < areaArray.length(); k++) {
                        area.add(new PickerNode<IPickerItemView>(new StringItemView(areaArray.optString(k))));
                    }
                    cnode.setNextLevel(area);
                    city.add(cnode);
                }
                pnode.setNextLevel(city);
                provinces.add(pnode);
            }

            return provinces;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void setOnCitySelectListener(OnCitySelectListener onCitySelectListener) {
        mOnCitySelectListener = onCitySelectListener;
    }

    public interface OnCitySelectListener {
        void OnCitySelected(ChineseCityWheelPicker view, String province, String city, String area);
    }
}
