package com.neteast.cloudtv2.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.neteast.cloudtv2.R;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-25 */
public class Tools {

	public static String CorrectPath(String path) {
		if (path.endsWith("/")) {
			return path;
		} else {
			return path + "/";
		}
	}

	/** 传入的日期格式 yyyy-MM-dd (如 2011-12-21)
	 * 
	 * @param Date */
	public static int[] parseDate(Context context, String Date) {
		int[] dates = new int[3];
		try {
			dates[0] = Integer.parseInt(Date.substring(0, 4));
			dates[1] = Integer.parseInt(Date.substring(5, 7));
			dates[2] = Integer.parseInt(Date.substring(8, 10));
		} catch (Exception e) {
			Toast.makeText(context, "日期格式有误", Toast.LENGTH_SHORT).show();
			return null;
		}
		return dates;
	}

	public interface ItemSelectListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id);
	}

	public static void showDropList(Context context, final TextView anchor, final List<String> dates,
			final ItemSelectListener itemSelectListener) {
		int dropDownlayout = R.layout.drop_down_list_layout_230;
		if (180 == anchor.getLayoutParams().width)
			dropDownlayout = R.layout.drop_down_list_layout_180;
		View contentView = LayoutInflater.from(context).inflate(dropDownlayout, null);
		final PopupWindow dropDownList = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		dropDownList.setOutsideTouchable(false);
		dropDownList.setFocusable(true);
		dropDownList.setBackgroundDrawable(new BitmapDrawable());
		ListView itemlist = (ListView) contentView.findViewById(R.id.item_list);
		itemlist.setAdapter(new ArrayAdapter<String>(context, R.layout.drop_down_list_item, R.id.item_text, dates));
		itemlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				anchor.setText(dates.get(position));
				dropDownList.dismiss();
				if (itemSelectListener != null)
					itemSelectListener.onItemClick(parent, view, position, id);
			}
		});
		dropDownList.showAsDropDown(anchor, 0, 0);
	}

	private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	
	public static String encodeBase64(String data) {
		return encodeBase64(data.getBytes());
	}
	
	/**
	 * 编码
	 * @param data
	 * @return
	 */
	public static String encodeBase64(byte[] data) {
		int start = 0;
		int len = data.length;
		StringBuffer buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;
		int n = 0;

		while (i <= end) {
			int d = ((((int) data[i]) & 0x0ff) << 16) | ((((int) data[i + 1]) & 0x0ff) << 8) | (((int) data[i + 2]) & 0x0ff);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append(legalChars[d & 63]);

			i += 3;

			if (n++ >= 14) {
				n = 0;
				buf.append(" ");
			}
		}

		if (i == start + len - 2) {
			int d = ((((int) data[i]) & 0x0ff) << 16) | ((((int) data[i + 1]) & 255) << 8);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append("=");
		} else if (i == start + len - 1) {
			int d = (((int) data[i]) & 0x0ff) << 16;

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append("==");
		}

		return buf.toString();
	}

	private static int decode(char c) {
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;
		else
			switch (c) {
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				throw new RuntimeException("unexpected code: " + c);
			}
	}

	/**
	 * 解码
	 * @param s
	 * @return
	 */
	public static byte[] decodeBase64(String s) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(s, bos);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		byte[] decodedBytes = bos.toByteArray();
		try {
			bos.close();
			bos = null;
		} catch (IOException ex) {
			System.err.println("Error while decoding BASE64: " + ex.toString());
		}
		return decodedBytes;
	}

	private static void decode(String s, OutputStream os) throws IOException {
		int i = 0;

		int len = s.length();

		while (true) {
			while (i < len && s.charAt(i) <= ' ')
				i++;

			if (i == len)
				break;

			int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(s.charAt(i + 2)) << 6)
					+ (decode(s.charAt(i + 3)));

			os.write((tri >> 16) & 255);
			if (s.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (s.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);

			i += 4;
		}
	}
}
