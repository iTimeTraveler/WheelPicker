package io.itimetraveler.widget.pickerselector;

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

import io.itimetraveler.widget.model.BasePickerItemView;
import io.itimetraveler.widget.model.IPickerItemView;
import io.itimetraveler.widget.pickerselector.R;

/**
 * Created by iTimeTraveler on 2018/9/16.
 */

class FlagItemView extends BasePickerItemView<String> {

    private String name;
    private String path;

    private int mDefaultColor = 0xFFAAAAAA;
    private int mSelectColor = 0xFF333333;

    public FlagItemView(String name, String path) {
        super(name);
        this.name = name;
        this.path = path;
    }

    private Bitmap getImageFromAssetsFile(Context context, String fileName) {
        InputStream istr = null;
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            istr = am.open(fileName);

            // Calculate inSampleSize
            options.inSampleSize = 6;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            image = BitmapFactory.decodeStream(istr, null, options);
            istr.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LinearLayout root = (LinearLayout) inflater.inflate(R.layout.flag_item, null);
        TextView textView = root.findViewById(R.id.name);
        ImageView imageView = root.findViewById(R.id.img);

        textView.setText(name);
        textView.setPadding(20, 3, 20, 3);
        //选中颜色
        int[] colors = new int[] {mSelectColor, mDefaultColor};
        int[][] states = {{android.R.attr.state_selected}, {}};
        textView.setTextColor(new ColorStateList(states, colors));
        imageView.setImageBitmap(getImageFromAssetsFile(parent.getContext(), path));

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.textView = textView;
        viewHolder.imageView = imageView;
        root.setTag(viewHolder);
        return root;
    }

    @Override
    public void onBindView(ViewGroup parent, View convertView, int position) {
        Object object = convertView.getTag();
        if(object instanceof ViewHolder){
            ((ViewHolder) object).textView.setText(name);
            ((ViewHolder) object).imageView.setImageBitmap(getImageFromAssetsFile(parent.getContext(), path));
        }
    }

    private static class ViewHolder{
        TextView textView;
        ImageView imageView;
    }
}
