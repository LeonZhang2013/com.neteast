package com.lib.appstore;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHander extends DefaultHandler {
	
	
	private String tagName = "";
	public final String APP_VERSIONCODE = "server";
	public final String APP_VERSIOMNAME = "userver";
	private Map<String, Object> map;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String dataString;
		if (APP_VERSIONCODE.equals(tagName)) {
			dataString = new String(ch, start, length);
			map.put("server", dataString);
		} else if (APP_VERSIOMNAME.equals(tagName)) {
			dataString = new String(ch, start, length);
			map.put("userver", dataString);
		}
	}

	public void setmap() {
		map = new HashMap<String, Object>();
	}

	public Map<String, Object> getmap() {
		return map;
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		tagName = "";
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tagName = localName;
	}
}