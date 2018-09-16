package com.itimetraveler.widget.demo;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import io.itimetraveler.widget.model.IPickerData;

/**
 * Created by iTimeTraveler on 2018/9/16.
 */

public class FlagData implements IPickerData {

    private String name;
    private String path;

    private int mDefaultColor = 0xFFAAAAAA;
    private int mSelectColor = 0xFF333333;

    public FlagData(String name, String path) {
        this.name = name;
        this.path = path;
    }

    private Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LinearLayout root = (LinearLayout) inflater.inflate(R.layout.activity_flag_item, null);
        TextView textView = root.findViewById(R.id.name);
        ImageView imageView = root.findViewById(R.id.img);

        textView.setText(name);
        //选中颜色
        int[] colors = new int[] {mSelectColor, mDefaultColor};
        int[][] states = {{android.R.attr.state_selected}, {}};
        textView.setTextColor(new ColorStateList(states, colors));
        imageView.setImageBitmap(getImageFromAssetsFile(parent.getContext(), path));
        return root;
    }

    @Override
    public void onBindView(ViewGroup parent, View view, int position) {
        if (view instanceof LinearLayout) {
            TextView textView = view.findViewById(R.id.name);
            ImageView imageView = view.findViewById(R.id.img);

            textView.setText(name);
            imageView.setImageBitmap(getImageFromAssetsFile(parent.getContext(), path));
        }
    }
}
