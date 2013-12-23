package com.neteast.clouddisk.activity;



import com.neteast.clouddisk.R;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class UpgradeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgradelayout);
	}

	public void returnOnclick(View v) {
		RelativeLayout rl = (RelativeLayout) UpgradeActivity.this.getParent()
				.getWindow().findViewById(R.id.datacontainer);
		rl.removeAllViews();
		Intent intent = new Intent(UpgradeActivity.this, SettingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window sub = ((ActivityGroup) UpgradeActivity.this.getParent())
				.getLocalActivityManager().startActivity("settingactivity",
						intent);
		View view = sub.getDecorView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		view.setLayoutParams(params);
		rl.addView(view);

	}
}
