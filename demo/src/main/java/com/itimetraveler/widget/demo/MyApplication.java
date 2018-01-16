package com.itimetraveler.widget.demo;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		TimeUtils.getInstance().setStartTime();
	}
}
