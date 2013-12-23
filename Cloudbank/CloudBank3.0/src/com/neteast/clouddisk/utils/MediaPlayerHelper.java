package com.neteast.clouddisk.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.neteast.clouddisk.activity.VideoPlaybackActivity;

public class MediaPlayerHelper {

	public static void play(Context ctx, String url, String movieid, String videoid, String isonline) {
		url = url.trim();

		
		// url = "http://192.168.19.105/public/video/2012.mp4";
		// url="http://218.108.168.172:9203/000000000000000000000000000000000000000000000000372755A37A14E329204D27FAEA28991C13C434120/url.mp4";

		/*
		 * System.out.println("MediaPlayerHelpter  url = " + url); Intent itent = new Intent(Intent.ACTION_VIEW); Uri uri =
		 * Uri.parse(url); itent.setData(uri); itent.setType("video/*"); //itent.setDataAndType(uri, "video/*");
		 * ctx.startActivity(itent); //Toast.makeText(ctx, url, Toast.LENGTH_LONG).show();
		 */

		Intent it = new Intent();
		it.setClass(ctx, VideoPlaybackActivity.class);
		Uri uri = Uri.parse(url);
		it.setData(uri);
		// it.putExtra("position", 0);
		// it.putExtra("playType", 4);
		ctx.startActivity(it);

		/** 这是云盘高清、在线播放用户点播行为做统计，与DataAcqusition类中写的重复，故此屏蔽掉 */
		// if(!isonline.equals("0")){
		// ((DownLoadApplication)ctx.getApplicationContext()).playorder(movieid,videoid,isonline);
		// }
	}

	public static void play(Context ctx, String url, String title) {
		// url =
		// "http://115.238.175.92/4/vlive.qqvideo.tc.qq.com/8ZMJZ33i7Uw.mp4?vkey=59CAA04026A870712CE57D1C877626E32E28137A76EEC391E66A44BF50510DC3CAD55668FF469694&amp;level=1";

		/*
		 * Intent itent = new Intent(Intent.ACTION_VIEW); Uri uri = Uri.parse(url); itent.setDataAndType(uri, "video/mp4");
		 * ctx.startActivity(itent); Toast.makeText(ctx, url, Toast.LENGTH_LONG).show();
		 */
		System.out.println("MediaPlayerHelpter  url = " + url);

		Intent it = new Intent();
		it.setClass(ctx, VideoPlaybackActivity.class);
		Uri uri = Uri.parse(url);
		it.setData(uri);
		it.putExtra("position", 0);
		it.putExtra("playType", 4);
		it.putExtra("title", title);
		ctx.startActivity(it);

	}

	public static void playFlash(Context ctx, String url, String title) {
		Intent it = new Intent();
		// it.setClass(ctx,FlashViewActivity.class);
		it.putExtra("url", url);
		it.putExtra("title", title);
		ctx.startActivity(it);
	}

	public static void playMusic(Context ctx, String url, String title, int pos) {
		if (url != null) {
			Intent it = new Intent();
			// it.setClass(ctx,MusicPlaybackActivity.class);
			Uri uri = Uri.parse(url);
			it.setData(uri);
			it.putExtra("pos", pos);
			it.putExtra("playType", 5);
			it.putExtra("title", title);
			ctx.startActivity(it);
			Log.d("MediaPlayHelper", "play:" + title + ", " + url);
		}
	}

	public static void playPicture(Context ctx, String url, String title) {
		if (url != null) {
			Intent it = new Intent();
			// it.setClass(ctx,ImageSwitcher1.class);
			Uri uri = Uri.parse(url);
			it.setData(uri);
			// it.putExtra("pos", pos);
			it.putExtra("playType", 4);
			it.putExtra("title", title);
			ctx.startActivity(it);
			Log.d("MediaPlayHelper", "play:" + title + ", " + url);
		}
	}
}
