package com.hs.utils;

import com.hs.activity.AppDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class GuestEventListener implements OnGestureListener {
	private String id;
	public void setId(String id) {
		this.id = id;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private Context context;
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Intent intent = new Intent(context,AppDetailActivity.class);
		intent.putExtra("id", id);
		context.startActivity(intent);
		return true;
	}

}
