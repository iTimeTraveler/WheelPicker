package com.itimetraveler.widget.demo;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by iTimeTraveler on 2019/4/21.
 */

public class DialogUtil {

    public static void showDialog(Context context, String title, View v) {
        final Context mContext = context;
        v.setPadding(20, 20, 20, 20);

        final Dialog bottomDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_content_normal, null);

        TextView titleView = contentView.findViewById(R.id.title);
        titleView.setText(title);

        View confrimView = contentView.findViewById(R.id.button_confirm);
        View cancelView = contentView.findViewById(R.id.button_cancel);
        LinearLayout content = contentView.findViewById(R.id.content);
        confrimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });

        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
        content.removeAllViews();
        content.addView(v);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.show();
    }
}
