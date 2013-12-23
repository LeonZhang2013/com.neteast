package com.neteast.cloudtv2.bean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-2-22 */
public class VideoBean {

	private int id;
	private String name;
	private String imagePath;
	private String playTime;
	private String descrip;
	private String playPath;

	public String getDescrip() {
		return descrip;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

	public String getPlayPath() {
		return playPath;
	}

	public void setPlayPath(String playPath) {
		this.playPath = playPath;
	}

	public static List<VideoBean> parserData(InputStream in) {

		List<VideoBean> list = new ArrayList<VideoBean>();
		VideoBean bean = null;

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(in, "UTF-8");
			int event = parser.getEventType();

			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String tagStr = parser.getName();
					if ("item".equals(tagStr)) {
						bean = new VideoBean();
					} else if ("title".equals(tagStr)) {
						if (bean != null)
							bean.setName(parser.nextText());
					}else if ("image".equals(tagStr)) {
						if (bean != null)
							bean.setImagePath(parser.nextText());
					}else if ("time".equals(tagStr)) {
						if (bean != null)
							bean.setPlayTime(parser.nextText());
					}else if ("desc".equals(tagStr)) {
						if (bean != null)
							bean.setDescrip(parser.nextText());
					}else if ("url".equals(tagStr)) {
						if (bean != null)
							bean.setPlayPath(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					String endTagStr = parser.getName();
					if ("item".equals(endTagStr)) {
						list.add(bean);
					}
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
