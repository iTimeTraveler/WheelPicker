package io.itimetraveler.widget.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by iTimeTraveler on 2018/9/16.
 */

public class ImageData implements IPickerData {

    private int resId;
    private String path;

    public ImageData(int resId) {
        this.resId = resId;
    }

    public ImageData(String path) {
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
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setImageBitmap(getImageFromAssetsFile(parent.getContext(), path));
        return imageView;
    }

    @Override
    public void onBindView(ViewGroup parent, View view, int position) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(getImageFromAssetsFile(parent.getContext(), path));
        }
    }
}
