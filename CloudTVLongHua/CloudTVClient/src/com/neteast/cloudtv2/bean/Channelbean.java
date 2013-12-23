package com.neteast.cloudtv2.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-25 */
public class Channelbean {

	private String icon;
	private String name;
	private String content;
	private String m3u8;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getM3u8() {
		return m3u8;
	}

	public void setM3u8(String m3u8) {
		this.m3u8 = m3u8;
	}

	@Override
	public String toString() {
		return "Channelbean [icon=" + icon + ", name=" + name + ", content=" + content + ", m3u8=" + m3u8 + "]";
	}

	public static List<Channelbean> parserData(InputStream in) throws XmlPullParserException, IOException {

		List<Channelbean> list = new ArrayList<Channelbean>();
		Channelbean bean = null;

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(in, "UTF-8");
		int event = parser.getEventType();

		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				String tagStr = parser.getName();
				if ("item".equals(tagStr) || "program".equals(tagStr)) {
					bean = new Channelbean();
					int count = parser.getAttributeCount();
					for (int i = 0; i < count; i++) {
						if ("name".equals(parser.getAttributeName(i))) {
							bean.setName(parser.getAttributeValue(i));
						} else if ("id".equals(parser.getAttributeName(i))) {
							bean.setM3u8(parser.getAttributeValue(i));
						} else if ("logo".equals(parser.getAttributeName(i))) {
							bean.setIcon(parser.getAttributeValue(i));
						}
					}
				} else if ("title".equals(tagStr)) {
					if (bean != null)
						bean.setName(parser.nextText());
				} else if ("image".equals(tagStr)) {
					if (bean != null)
						bean.setIcon(parser.nextText());
				} else if ("contenturl".equals(tagStr)) {
					if (bean != null)
						bean.setContent(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				String endTagStr = parser.getName();
				if ("item".equals(endTagStr) || "program".equals(endTagStr)) {
					list.add(bean);
				}
				break;
			}
			event = parser.next();
		}
		return list;
	}

}
