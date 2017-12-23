package com.itimetraveler.widget.demo;


import android.util.Log;

public class TimeUtils {

	private static TimeUtils mInstance;
	private long mStartTime;
	private long mEndTime;

	public static TimeUtils getInstance(){
		if(mInstance == null){
			mInstance = new TimeUtils();
		}
		return mInstance;
	}

	public void setStartTime() {
		this.mStartTime = System.currentTimeMillis();
	}

	public void setEndTime(String src) {
		this.mEndTime = System.currentTimeMillis();
		Log.v("TimeUtils >> " + src, "" + (mEndTime - mStartTime));
	}

}
