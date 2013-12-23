package com.hs.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hs.activity.DownLoadApplication;
import com.hs.activity.R;
import com.hs.handler.DownloadProgressHandlerX;
import com.hs.params.Params;
import com.lib.appstore.LibAppstore;

public class UIHelper {
	public static void addDataToLayout(List<Map<String, Object>> list, LibAppstore lib, Context context,
			final GestureDetector clickDetecotor, final GuestEventListener gel, ViewFlipper vf) {
		int i = 0;
		int j = 0;
		int lastIndex = vf.getChildCount() - 1;
		LinearLayout linear = (LinearLayout) vf.getChildAt(lastIndex);
		for (int k = 0; k < 5; k++) {
			((LinearLayout) linear.getChildAt(k)).removeAllViews();
		}
		for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
			RelativeLayout elementlayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.elementlayout, null);
			elementlayout.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			ImageView iv = (ImageView) elementlayout.getChildAt(0);//
			Map<String, Object> map = iterator.next();
			lib.DisplayImage((String) map.get("image"), iv);
			elementlayout.setTag(map);
			elementlayout.setOnTouchListener(new OnTouchListener() {
				@SuppressWarnings("unchecked")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Map<String, Object> map = (Map<String, Object>) v.getTag();
					gel.setId((String) map.get("id"));
					return clickDetecotor.onTouchEvent(event);
				}
			});
			LinearLayout textAndRate = (LinearLayout) elementlayout.getChildAt(1);
			TextView name = (TextView) textAndRate.getChildAt(0);// 名称
			TextView cate = (TextView) textAndRate.getChildAt(1);// 分类
			LinearLayout ratebarAndTimes = (LinearLayout) textAndRate.getChildAt(2);
			RatingBar rb = (RatingBar) ratebarAndTimes.getChildAt(0);
			// TextView times = (TextView) ratebarAndTimes.getChildAt(1);
			// System.out.println("rating:"+map.get("rating"));
			if (map.get("rating").equals(""))
				rb.setRating(0);
			else
				rb.setRating(Float.parseFloat((String) map.get("rating")));

			name.setText((String) map.get("title"));
			cate.setText(Params.getTypeName((String) map.get("type")) + map.get("ctitle"));
			i++;
			if (i > Params.NUM_PER_ROW) {
				i = 1;
				j++;
			}
			((LinearLayout) linear.getChildAt(j)).addView(elementlayout);
			if (!iterator.hasNext()) {
				int z = Params.NUM_PER_ROW - i;
				for (int x = 0; x < z; x++) {
					RelativeLayout reduElement = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.elementlayout,
							null);
					reduElement.removeAllViews();
					reduElement.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
					((LinearLayout) linear.getChildAt(j)).addView(reduElement);
				}
			}
		}
	}

	public static void addDataToViewPagerLayout(List<Map<String, Object>> list, LibAppstore lib, Context context,
			final GestureDetector clickDetecotor, final GuestEventListener gel, ArrayList<View> views) {
		int i = 0;
		int j = 0;

		int lastIndex = views.size() - 1;
		LinearLayout linear = (LinearLayout) views.get(lastIndex);
		for (int k = 0; k < 5; k++) {
			((LinearLayout) linear.getChildAt(k)).removeAllViews();
		}
		for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
			RelativeLayout elementlayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.elementlayout, null);
			elementlayout.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			ImageView iv = (ImageView) elementlayout.getChildAt(0);//
			Map<String, Object> map = iterator.next();
			lib.DisplayImage((String) map.get("image"), iv);
			elementlayout.setTag(map);
			elementlayout.setOnTouchListener(new OnTouchListener() {
				@SuppressWarnings("unchecked")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Map<String, Object> map = (Map<String, Object>) v.getTag();
					gel.setId((String) map.get("id"));
					return clickDetecotor.onTouchEvent(event);
				}
			});
			LinearLayout textAndRate = (LinearLayout) elementlayout.getChildAt(1);
			TextView name = (TextView) textAndRate.getChildAt(0);// 名称
			TextView cate = (TextView) textAndRate.getChildAt(1);// 分类
			LinearLayout ratebarAndTimes = (LinearLayout) textAndRate.getChildAt(2);
			RatingBar rb = (RatingBar) ratebarAndTimes.getChildAt(0);
			// TextView times = (TextView) ratebarAndTimes.getChildAt(1);
			// System.out.println("rating:"+map.get("rating"));
			if (map.get("rating").equals(""))
				rb.setRating(0);
			else
				rb.setRating(Float.parseFloat((String) map.get("rating")));

			name.setText((String) map.get("title"));
			cate.setText(Params.getTypeName((String) map.get("type")) + map.get("ctitle"));
			i++;
			if (i > Params.NUM_PER_ROW) {
				i = 1;
				j++;
			}
			((LinearLayout) linear.getChildAt(j)).addView(elementlayout);
			if (!iterator.hasNext()) {
				int z = Params.NUM_PER_ROW - i;
				for (int x = 0; x < z; x++) {
					RelativeLayout reduElement = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.elementlayout,
							null);
					reduElement.removeAllViews();
					reduElement.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
					((LinearLayout) linear.getChildAt(j)).addView(reduElement);
				}
			}
		}
	}

	/** 根据应用计算页数
	 * 
	 * @param count
	 * @return 页数 */
	public static int getTotalPage(int total) {
		int totalPage = 0;
		int a = total / Params.NUM_PER_PAGE;
		if (total % Params.NUM_PER_PAGE != 0) {
			totalPage = a + 1;
		} else {
			totalPage = a;
		}
		return totalPage;
	}

	public static void setCustomTabColor(TextView tv, LinearLayout tab, Resources res) {
		int count = tab.getChildCount();
		ColorStateList cs1 = res.getColorStateList(R.color.customtabtextcolor1);// 选中字体颜色
		ColorStateList cs2 = res.getColorStateList(R.color.customtabtextcolor2);// 为选中字体颜色
		Drawable d1 = res.getDrawable(R.drawable.selecttextviewbg);
		// Drawable d2 = res.getDrawable(R.color.customertabbg);
		for (int i = 0; i < count; i++) {
			TextView t = (TextView) ((LinearLayout) tab.getChildAt(i)).getChildAt(0);
			t.setBackgroundDrawable(null);
			t.setTextColor(cs2);
		}
		tv.setTextColor(cs1);
		tv.setBackgroundDrawable(d1);
	}

	public static void addDownload2layout(AppData appData, RelativeLayout rl, int x, View view) {
		ProgressBar pb = (ProgressBar) rl.getChildAt(0);
		TextView tv = (TextView) rl.getChildAt(1);
		DownloadProgressHandlerX handler = new DownloadProgressHandlerX(pb, x, view);
		appData.setHandler(handler);
		pb.setProgress(0);
		tv.setText("0%");
	}

	/** 加载安装数据
	 * 
	 * @param lib
	 * @param vf
	 * @param context
	 * @param list
	 * @param gel
	 * @param clickDetecotor */
	public static void addInstall2Layout(LibAppstore lib, ViewFlipper vf, Context context, List<Map<String, Object>> list,
			DownLoadApplication app, final GuestEventListener gel, final GestureDetector clickDetecotor) {
		LinearLayout vflayout = (LinearLayout) vf.getChildAt(vf.getChildCount() - 1);
		int index = 0;
		int x = 0;
		int current = 0;
		((LinearLayout) vflayout.getChildAt(0)).removeAllViews();
		((LinearLayout) vflayout.getChildAt(1)).removeAllViews();

		for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			LinearLayout linear = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.sixthdatalayout2, null);
			linear.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
			ImageView iv = (ImageView) linear.findViewById(R.id.elementimageView);
			// System.out.println("app name = " + (String) map.get("title"));
			if ((Integer) map.get("id") == -1) {
				PackageManager pm = context.getPackageManager();
				try {
					Drawable ico = pm.getApplicationIcon((String) map.get("package"));
					iv.setImageDrawable(ico);

				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				lib.DisplayImage((String) map.get("image"), iv);
			}
			TextView appName = (TextView) linear.findViewById(R.id.appname);
			appName.setText((String) map.get("title"));
			TextView appSize = (TextView) linear.findViewById(R.id.appsize);
			appSize.setText("大小：" + map.get("size") + "M");
			TextView appVersion = (TextView) linear.findViewById(R.id.appversion);
			appVersion.setText("版本：" + map.get("version"));
			Button bt = (Button) linear.findViewById(R.id.uninstallbutton);
			((LinearLayout) bt.getParent()).setTag(map);
			if (index < 5) {
				((LinearLayout) vflayout.getChildAt(0)).addView(linear);
				index++;
				x++;
			} else {
				if (x == 5) {
					x = 0;
				}
				current = 1;
				((LinearLayout) vflayout.getChildAt(1)).addView(linear);
				x++;
			}

		}
		if (x < 5) {
			for (int y = 0; y < (5 - x); y++) {
				LinearLayout ll = new LinearLayout(context);
				ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
				((LinearLayout) vflayout.getChildAt(current)).addView(ll);
			}
		}
	}

	/** 加载升级数据
	 * 
	 * @param lib
	 * @param vf
	 * @param context
	 * @param list
	 * @param gel
	 * @param clickDetecotor */
	public static void addUpgrade2Layout(LibAppstore lib, ViewFlipper vf, Context context, List<Map<String, Object>> list,
			DownLoadApplication app, int pageNum, final GuestEventListener gel, final GestureDetector clickDetecotor) {
		
		LinearLayout vflayout = (LinearLayout) vf.getChildAt(vf.getChildCount() - 1);
		int size = vflayout.getChildCount();
		for (int i = 0; i < size; i++) {
			((LinearLayout) vflayout.getChildAt(i)).removeAllViews();
		}
		int index = 0;
		int x = 0;
		int current = 0;
		int num = 0;
		for (Iterator<Map<String, Object>> iterator = list.listIterator((pageNum - 1) * 10); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			num++;
			if (num >= 10) {
				return;
			}
			LinearLayout linear = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.sixthdatalayout3, null);
			linear.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
			ImageView iv = (ImageView) linear.findViewById(R.id.elementimageView);
			lib.DisplayImage((String) map.get("image"), iv);
			TextView appName = (TextView) linear.findViewById(R.id.appname);
			appName.setText((String) map.get("title"));
			TextView appSize = (TextView) linear.findViewById(R.id.appsize);
			appSize.setText("大小：" + map.get("size") + "M");
			TextView appVersion = (TextView) linear.findViewById(R.id.appversion);
			appVersion.setText("版本：" + map.get("version"));
			Button upgradeBt = (Button) linear.findViewById(R.id.upgradebutton);
			((LinearLayout) upgradeBt.getParent()).setTag(map);
			Button ignoredButton = (Button) linear.findViewById(R.id.ignorebutton);
			Button cancelIgnoredButton = (Button) linear.findViewById(R.id.cancelIgnorebutton);
			if (Tools.isUpdate((Long) map.get("ignored"))) {
				ignoredButton.setVisibility(View.VISIBLE);
			} else {
				cancelIgnoredButton.setVisibility(View.VISIBLE);
			}
			if (index < 5) {
				((LinearLayout) vflayout.getChildAt(0)).addView(linear);
				index++;
				x++;
			} else {
				if (x == 5) {
					x = 0;
				}
				current = 1;
				((LinearLayout) vflayout.getChildAt(1)).addView(linear);
				x++;
			}

		}
		if (x < 5) {
			for (int y = 0; y < (5 - x); y++) {
				LinearLayout ll = new LinearLayout(context);
				ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
				((LinearLayout) vflayout.getChildAt(current)).addView(ll);
			}
		}
	}

	/** 根据行选择对应图片的id
	 * 
	 * @param i
	 * @return */
	public static int selectImage(int i) {
		int res = 0;
		switch (i) {
		case 0:
			res = R.drawable.one;
			break;
		case 1:
			res = R.drawable.two;
			break;
		case 2:
			res = R.drawable.three;
			break;
		case 3:
			res = R.drawable.four;
			break;
		case 4:
			res = R.drawable.five;
			break;
		case 5:
			res = R.drawable.six;
			break;
		case 6:
			res = R.drawable.seven;
			break;
		case 7:
			res = R.drawable.eight;
			break;
		case 8:
			res = R.drawable.nine;
			break;
		case 9:
			res = R.drawable.ten;
			break;
		}
		return res;
	}

	/** 创建悬浮提示窗口
	 * 
	 * @param context
	 * @param customeToast
	 * @return */
	public static WindowManager createToastWindow(Activity context, final LinearLayout customeToast, String appName) {
		final WindowManager wm = context.getWindowManager();
		try {
			wm.removeView(customeToast);
		} catch (Exception e) {

		}
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2007;
		wmParams.format = 1;
		wmParams.gravity = Gravity.TOP;
		wmParams.flags = 40;
		wmParams.width = android.view.ViewGroup.LayoutParams.FILL_PARENT;
		wmParams.height = 40;
		wm.addView(customeToast, wmParams); // 创建View
		((TextView) customeToast.getChildAt(0)).setText("(" + appName + ")已经加入下载列表点击查看");

		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				try {
					wm.removeView(customeToast);
				} catch (Exception e) {
				}
			}
		};
		Timer time = new Timer();
		time.schedule(tt, 3000);

		return wm;
	}

	public static void displayToast(String text, Context context) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void displayToast(String text, Context context, int times) {
		Toast.makeText(context, text, times).show();
	}
}
