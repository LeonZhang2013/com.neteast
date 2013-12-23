package com.neteast.cloudtv2.tools;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-10-23 */
public class XmlTools {

	public static <T> T parseSingle(InputStream in, Class<T> clazz) throws Exception {
		T bean = clazz.newInstance();
		XmlPullParser pull = Xml.newPullParser();
		pull.setInput(in, "UTF-8");
		int event = pull.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				Field f;
				try{
					f = clazz.getDeclaredField(pull.getName());
				}catch (Exception e) {
					break;
				}
				if (f != null) {
					f.setAccessible(true);
					String text = pull.nextText();
					if (text != null)
						f.set(bean, text);
				}
				break;
			default:
				break;
			}
			event = pull.next();
		}
		return bean;
	}

	
	public static <T extends Object> List<T> parseList(InputStream in, Class<T> clazz) throws Exception {
		XmlPullParser pull = Xml.newPullParser();
		pull.setInput(in, "UTF-8");
		int event = pull.getEventType();
		List<T> list = new ArrayList<T>();
		T bean = null;
		while (event != XmlPullParser.END_DOCUMENT) {
			String tag = pull.getName();
			switch (event) {
			case XmlPullParser.START_TAG:
				if("entity".equals(tag)){
					bean = clazz.newInstance();
					break;
				}
				Field f;
				try{
					f = clazz.getDeclaredField(pull.getName());
				}catch (Exception e) {
					break;
				}
				if (f != null) {
					f.setAccessible(true);
					String text = pull.nextText();
					if (text != null)
						f.set(bean, text);
				}
				break;
			case XmlPullParser.END_TAG:
				if("entity".equals(tag)){
					list.add(bean);
				}
			default:
				break;
			}
			event = pull.next();
		}
		return list;
	}

}
