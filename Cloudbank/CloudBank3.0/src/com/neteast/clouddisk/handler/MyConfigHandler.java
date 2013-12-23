package com.neteast.clouddisk.handler;

import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class MyConfigHandler extends DefaultHandler {

	private Map<String, Object> map;
	private String preTAG;
	
	
	public MyConfigHandler(Map<String, Object> map2) {
		// TODO Auto-generated constructor stub
		this.map = map2;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		preTAG = localName;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		preTAG = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String dataString;
		System.out.println("MyConfigHandler preTAG" +preTAG );
		if("versionUrl".equals(preTAG)){
			dataString = new String(ch, start, length);
			map.put("versionurl", dataString);
		}else if("apkUrl".equals(preTAG)){
			dataString = new String(ch, start, length);
			map.put("apkurl", dataString);
		}
	}
}
