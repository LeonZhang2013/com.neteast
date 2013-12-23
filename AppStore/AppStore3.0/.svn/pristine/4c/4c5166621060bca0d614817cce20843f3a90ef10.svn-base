package com.hs.domain;

import java.util.Map;

import com.hs.activity.PopWindowActivity;
import com.hs.handler.LoginHandler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-1-4 */
public class User {

	private static int userid = 0;
	private static String token;
	private static String username;
	/** 消息盒子内容提供地址 */
	public static String CONTENT_PROVIDER = "content://com.neteast.androidclient.newscenter/info";

	public static int getUserId() {
		return userid;
	}

	public static String getToken() {
		return token;
	}

	public static String getUserName() {
		return username;
	}

	/** 检查消息盒子是否登陆 成功返回 true 否者返回 false；̬ */
	public static void getUserInfo(Context mContext) {
		try{
			Uri uri = Uri.parse(CONTENT_PROVIDER);
			Cursor cursor = mContext.getContentResolver().query(uri, null, null,null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					userid = cursor.getInt(cursor.getColumnIndex("userid"));
					token = cursor.getString(cursor.getColumnIndex("token"));
					username = cursor.getString(cursor.getColumnIndex("account"));
				}
			}
			cursor.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
