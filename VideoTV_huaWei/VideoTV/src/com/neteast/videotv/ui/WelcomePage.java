package com.neteast.videotv.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xmlpull.v1.XmlPullParser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Xml;
import android.view.View;

import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.utils.Utils;

public class WelcomePage {

	private File CacheDir;
	private String ImageName = "Welcome.videotv";

	public WelcomePage() {
		CacheDir = new File(Environment.getExternalStorageDirectory(),
				"VideoTV");
		if (!CacheDir.exists()) {
			CacheDir.mkdirs();
		}
	}

	public void Loading(final View mStartImageLaytout) {
		Drawable drawable = Drawable.createFromPath(CacheDir + "/" + ImageName);
		if(drawable==null){
			mStartImageLaytout.setBackgroundResource(R.drawable.image_start);
		}else{
			mStartImageLaytout.setBackgroundDrawable(drawable);
		}
		new DownLoadImageTask().execute();
		new CountDownTimer(4000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				ObjectAnimator animator = ObjectAnimator.ofFloat(
						mStartImageLaytout, "alpha", 0f);
				animator.setDuration(2000);
				animator.addListener(new AnimatorListenerAdapter() {
					public void onAnimationEnd(Animator animation) {
						mStartImageLaytout.setVisibility(View.GONE);
						// mStartImage.setBackground(null);
					}
				});
				animator.start();
			}
		}.start();

	}


	class DownLoadImageTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String ImageNetPath;
			try {
				ImageNetPath = parseSingle(Utils.downloadUrl(TVApplication.API_HOME_WELCOME_IMAGE));
				File image = new File(CacheDir, ImageName);
				InputStream in = Utils.downloadUrl(ImageNetPath);
				saveFileLocal(in, image);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
	}

	public String parseSingle(InputStream in) throws Exception {
		String ImagePath = null;
		XmlPullParser pull = Xml.newPullParser();
		pull.setInput(in, "UTF-8");
		int event = pull.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			String tag = pull.getName();
			switch (event) {
			case XmlPullParser.START_TAG:
				if ("Image".equals(tag)) {
					ImagePath = pull.nextText();
					break;
				}
				break;
			}
			event = pull.next();
		}
		return ImagePath;
	}

	public static void saveFileLocal(InputStream is, File f) throws IOException {
		final int buffer_size = 1024;
		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} finally {
			if (os != null)
				os.close();
		}
	}



}
