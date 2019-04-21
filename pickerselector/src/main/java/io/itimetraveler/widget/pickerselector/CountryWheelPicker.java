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
public class CountryWheelPicker extends WheelPicker {

    private Context mContext;
    private OnCountrySelectListener mOnCountrySelectListener;

    public CountryWheelPicker(Context context) {
        this(context, null);
    }

    public CountryWheelPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountryWheelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        final List<PickerNode<IPickerItemView>> flags = getFlags();
        if (flags == null) {
            return;
        }

        PickerAdapter adapter = new PickerAdapter() {
            @Override
            public int numberOfComponentsInWheelPicker(WheelPicker wheelPicker) {
                return 2;
            }

            @Override
            public int numberOfRowsInComponent(int component) {
                int[] selectPos = CountryWheelPicker.this.getSelectedPositions();
                switch (component) {
                    case 0:
                        return flags.size();
                    case 1:
                        return flags.get(selectPos[0]).getNextLevel().size();
                }
                return 0;
            }

            @Override
            public View onCreateView(ViewGroup parent, int row, int component) {
                int[] selectPos = CountryWheelPicker.this.getSelectedPositions();
                switch (component) {
                    case 0:
                        return flags.get(row).getData().onCreateView(parent);
                    case 1:
                        return flags.get(selectPos[0]).getNextLevel().get(row).getData().onCreateView(parent);
                }
                return null;
            }

            @Override
            public void onBindView(ViewGroup parent, View convertView, int row, int component) {
                int[] selectPos = CountryWheelPicker.this.getSelectedPositions();
                switch (component) {
                    case 0:
                        flags.get(row).getData().onBindView(parent, convertView, row);
                        break;
                    case 1:
                        flags.get(selectPos[0]).getNextLevel().get(row).getData().onBindView(parent, convertView, row);
                        break;
                }
            }
        };

        setOptions(new PicketOptions.Builder()
                .linkage(true)
                .build());
        setAdapter(adapter);

        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker parentView, int[] position) {
                if (mOnCountrySelectListener != null) {
                    mOnCountrySelectListener.OnCountrySelected(CountryWheelPicker.this,
                            ((FlagItemView) flags.get(position[0]).getNextLevel().get(position[1]).getData()).getData());
                }
            }
        });
    }


    private List<PickerNode<IPickerItemView>> getFlags() {
        try {
            InputStream is = mContext.getAssets().open("flag.json");
            int length = is.available();
            byte[]  buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf8");

            List<PickerNode<IPickerItemView>> continents = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.optJSONArray("continents");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.optJSONObject(i);
                String pname = o.optString("name");
                PickerNode<IPickerItemView> pnode = new PickerNode<IPickerItemView>(new StringItemView(pname));

                List<PickerNode<IPickerItemView>> countries = new ArrayList<>();
                JSONArray cityArray = o.optJSONArray("countries");
                for (int j = 0; cityArray != null && j < cityArray.length(); j++) {
                    JSONObject c = cityArray.optJSONObject(j);
                    String cname = c.optString("name");
                    String cflag = c.optString("flag");
                    PickerNode<IPickerItemView> cnode = new PickerNode<IPickerItemView>(new FlagItemView(cname,"flags/" + cflag));
                    countries.add(cnode);
                }
                pnode.setNextLevel(countries);
                continents.add(pnode);
            }

            return continents;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOnCountrySelectListener(OnCountrySelectListener onCountrySelectListener) {
        mOnCountrySelectListener = onCountrySelectListener;
    }

    public interface OnCountrySelectListener {
        void OnCountrySelected(CountryWheelPicker view, String countrySelected);
    }
}
