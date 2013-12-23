package com.lib.appstore;

import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class MyHandler extends DefaultHandler {
	public static final String APP_VERSIONCODE = "server";
	public static final String APP_VERSIOMNAME = "userver";
	private Map<String, Object> map;
	private String preTAG;
	
	
	public MyHandler(Map<String, Object> map2) {
		// TODO Auto-generated constructor stub
		System.out.println("new handler");
		this.map = map2;
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		preTAG = localName;
		for(int i=0;i<attributes.getLength();i++){
			System.out.println(attributes.getLocalName(i)+"**********"+attributes.getValue(i));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		//preTAG = "";
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String dataString;
		System.out.println("preTag"+preTAG);
		if(APP_VERSIONCODE.equals(preTAG)){
			dataString = new String(ch, start, length);
			map.put("server", dataString);
		}else if(APP_VERSIOMNAME.equals(preTAG)){
			dataString = new String(ch, start, length);
			map.put("userver", dataString);
		}
	}
}
